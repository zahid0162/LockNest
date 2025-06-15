package com.zahid.locknest.util

import android.content.Context
import android.net.Uri
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.zahid.locknest.data.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfExporter @Inject constructor(
    private val context: Context,
    private val passwordRepository: PasswordRepository
) {
    /**
     * Exports all passwords to a PDF file
     * @param uri The destination URI to save the PDF
     * @param includePasswords Whether to include actual password values in the export
     * @return Result with success message or error
     */
    suspend fun exportToPdf(uri: Uri, includePasswords: Boolean): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Get all passwords
            val passwords = passwordRepository.getAllPasswords().first()
            
            // Create PDF document
            val document = Document()
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                PdfWriter.getInstance(document, outputStream)
                document.open()
                
                // Add title
                val title = Paragraph("LockNest Password Export", 
                    Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, BaseColor.DARK_GRAY))
                title.alignment = Element.ALIGN_CENTER
                document.add(title)
                
                // Add timestamp (using SimpleDateFormat for compatibility)
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val dateText = Paragraph("Generated on: $timestamp", 
                    Font(Font.FontFamily.HELVETICA, 10f, Font.ITALIC, BaseColor.GRAY))
                dateText.alignment = Element.ALIGN_CENTER
                document.add(dateText)
                document.add(Chunk.NEWLINE)
                
                // Add security notice
                if (includePasswords) {
                    val warning = Paragraph("SECURITY WARNING: This document contains your actual passwords. Keep it secure!", 
                        Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.RED))
                    warning.alignment = Element.ALIGN_CENTER
                    document.add(warning)
                    document.add(Chunk.NEWLINE)
                }
                
                // Group passwords by category
                val passwordsByCategory = passwords.groupBy { it.category }
                
                // Add each category
                passwordsByCategory.forEach { (category, categoryPasswords) ->
                    // Add category header
                    val categoryHeader = Paragraph(category, 
                        Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, BaseColor.BLUE))
                    document.add(categoryHeader)
                    document.add(Chunk.NEWLINE)
                    
                    // Create table
                    val table = PdfPTable(if (includePasswords) 4 else 3)
                    table.widthPercentage = 100f
                    
                    // Add table headers
                    val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
                    addCell(table, "Title", headerFont)
                    addCell(table, "Username", headerFont)
                    if (includePasswords) {
                        addCell(table, "Password", headerFont)
                    }
                    addCell(table, "Website", headerFont)
                    
                    // Add password rows
                    val cellFont = Font(Font.FontFamily.HELVETICA, 10f)
                    categoryPasswords.forEach { password ->
                        addCell(table, password.title, cellFont)
                        addCell(table, password.username, cellFont)
                        if (includePasswords) {
                            addCell(table, password.password, cellFont)
                        }
                        addCell(table, password.website ?: "", cellFont)
                    }
                    
                    document.add(table)
                    document.add(Chunk.NEWLINE)
                }
                
                document.close()
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))
            
            Result.success("Passwords exported successfully to PDF")
        } catch (e: Exception) {
            Result.failure(Exception("PDF export failed: ${e.message}"))
        }
    }
    
    private fun addCell(table: PdfPTable, text: String, font: Font) {
        val cell = PdfPCell(Phrase(text, font))
        cell.paddingTop = 5f
        cell.paddingBottom = 5f
        cell.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(cell)
    }
} 