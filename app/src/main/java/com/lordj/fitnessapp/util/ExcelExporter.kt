package com.lordj.fitnessapp.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.lordj.fitnessapp.data.model.SetLog
import com.lordj.fitnessapp.data.model.WorkoutSession
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExcelExporter {

    fun export(
        context: Context,
        sessions: List<WorkoutSession>,
        sets: List<SetLog>
    ): File {
        val file = File(context.cacheDir, "fitness_report_${System.currentTimeMillis()}.xlsx")
        val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Build data for each sheet
        val summaryRows = buildSummaryRows(sessions, dateFmt)
        val detailRows = buildDetailRows(sets, timeFmt)
        val prRows = buildPRRows(sets, dateFmt)

        // Shared strings
        val sharedStrings = mutableListOf<String>()
        fun str(s: String): Int {
            val idx = sharedStrings.indexOf(s)
            if (idx >= 0) return idx
            sharedStrings.add(s)
            return sharedStrings.size - 1
        }

        val sheet1Xml = buildSheetXml(summaryRows, ::str)
        val sheet2Xml = buildSheetXml(detailRows, ::str)
        val sheet3Xml = buildSheetXml(prRows, ::str)
        val sharedStringsXml = buildSharedStringsXml(sharedStrings)

        ZipOutputStream(FileOutputStream(file)).use { zip ->
            writeEntry(zip, "[Content_Types].xml", contentTypesXml())
            writeEntry(zip, "_rels/.rels", relsXml())
            writeEntry(zip, "xl/workbook.xml", workbookXml())
            writeEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelsXml())
            writeEntry(zip, "xl/worksheets/sheet1.xml", sheet1Xml)
            writeEntry(zip, "xl/worksheets/sheet2.xml", sheet2Xml)
            writeEntry(zip, "xl/worksheets/sheet3.xml", sheet3Xml)
            writeEntry(zip, "xl/sharedStrings.xml", sharedStringsXml)
            writeEntry(zip, "xl/styles.xml", stylesXml())
        }
        return file
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Fitness Tracker – Reporte")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir reporte"))
    }

    private fun buildSummaryRows(sessions: List<WorkoutSession>, fmt: SimpleDateFormat): List<List<Any>> {
        val rows = mutableListOf<List<Any>>()
        rows.add(listOf("Fecha", "Rutina", "Duración (min)", "Series", "Volumen (kg)"))
        sessions.forEach { s ->
            rows.add(listOf(
                fmt.format(Date(s.startTime)),
                s.workoutName,
                s.durationMinutes.toString(),
                s.totalSets.toString(),
                s.totalVolumeKg.toString()
            ))
        }
        return rows
    }

    private fun buildDetailRows(sets: List<SetLog>, fmt: SimpleDateFormat): List<List<Any>> {
        val rows = mutableListOf<List<Any>>()
        rows.add(listOf("Fecha", "Ejercicio", "Serie", "Reps", "Peso (kg)", "Volumen (kg)", "e1RM est.", "Calentamiento"))
        sets.sortedBy { it.completedAt }.forEach { s ->
            rows.add(listOf(
                fmt.format(Date(s.completedAt)),
                s.exerciseName,
                s.setNumber.toString(),
                s.reps.toString(),
                s.weightKg.toString(),
                s.volume.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                s.e1RM.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                if (s.isWarmup) "Sí" else "No"
            ))
        }
        return rows
    }

    private fun buildPRRows(sets: List<SetLog>, fmt: SimpleDateFormat): List<List<Any>> {
        val rows = mutableListOf<List<Any>>()
        rows.add(listOf("Ejercicio", "Peso Máx (kg)", "e1RM Máx est.", "Total Series", "Última vez"))
        val bySets = sets.filter { !it.isWarmup }.groupBy { it.exerciseName }
        bySets.entries.sortedByDescending { it.value.maxOf { s -> s.weightKg } }.forEach { (name, exSets) ->
            val maxW = exSets.maxOf { it.weightKg }
            val maxE = exSets.maxOf { it.e1RM }
            val last = exSets.maxOf { it.completedAt }
            rows.add(listOf(name, maxW.toString(), maxE.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                exSets.size.toString(), fmt.format(Date(last))))
        }
        return rows
    }

    private fun buildSheetXml(rows: List<List<Any>>, str: (String) -> Int): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
        sb.append("<sheetData>")
        rows.forEachIndexed { rowIdx, cols ->
            val isHeader = rowIdx == 0
            sb.append("""<row r="${rowIdx + 1}">""")
            cols.forEachIndexed { colIdx, value ->
                val colLetter = ('A' + colIdx).toString()
                val cellRef = "$colLetter${rowIdx + 1}"
                val s = if (isHeader) " s=\"1\"" else ""
                sb.append("""<c r="$cellRef" t="s"$s><v>${str(value.toString())}</v></c>""")
            }
            sb.append("</row>")
        }
        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    private fun buildSharedStringsXml(strings: List<String>): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<sst xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" count="${strings.size}" uniqueCount="${strings.size}">""")
        strings.forEach { s ->
            sb.append("<si><t>${s.xmlEscape()}</t></si>")
        }
        sb.append("</sst>")
        return sb.toString()
    }

    private fun contentTypesXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/worksheets/sheet3.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/sharedStrings.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""

    private fun relsXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""

    private fun workbookXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Resumen Sesiones" sheetId="1" r:id="rId1"/>
    <sheet name="Detalle Series" sheetId="2" r:id="rId2"/>
    <sheet name="Records Personales" sheetId="3" r:id="rId3"/>
  </sheets>
</workbook>"""

    private fun workbookRelsXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet3.xml"/>
  <Relationship Id="rId4" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings" Target="sharedStrings.xml"/>
  <Relationship Id="rId5" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    private fun stylesXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="2">
    <font><sz val="11"/><name val="Calibri"/></font>
    <font><b/><sz val="11"/><name val="Calibri"/><color rgb="FFFFFFFF"/></font>
  </fonts>
  <fills count="3">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFFF6D00"/></patternFill></fill>
  </fills>
  <borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs count="2">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="0" xfId="0" applyFont="1" applyFill="1"/>
  </cellXfs>
</styleSheet>"""

    private fun writeEntry(zip: ZipOutputStream, name: String, content: String) {
        zip.putNextEntry(ZipEntry(name))
        zip.write(content.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun String.xmlEscape() = replace("&", "&amp;").replace("<", "&lt;")
        .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;")
}
