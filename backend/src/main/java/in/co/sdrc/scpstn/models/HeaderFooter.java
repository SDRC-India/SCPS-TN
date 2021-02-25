package in.co.sdrc.scpstn.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.springframework.util.ResourceUtils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import in.co.sdrc.scpstn.domain.Agency;

/** Inner class to add a header and a footer. */
public class HeaderFooter extends PdfPageEventHelper {
	/** Alternating phrase for the header. */
	Phrase[] header = new Phrase[2];
	/** Current page number (will be reset for every chapter). */
	int pagenumber;
	private ServletContext context;
	private Agency agency;

	public HeaderFooter(ServletContext context, Agency agency) {
		this.context = context;
		this.agency = agency;
	}

	/**
	 * Initialize one of the headers.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		header[0] = new Phrase("Movie history");
	}

	/**
	 * Initialize one of the headers, based on the chapter title; reset the page
	 * number.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document, float, com.itextpdf.text.Paragraph)
	 */
	public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
		header[1] = new Phrase(title.getContent());
		pagenumber = 1;

	}

	/**
	 * Increase the page number.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onStartPage(PdfWriter writer, Document document) {
		pagenumber++;

		Image image;
		try {
			
			image = Image.getInstance(ResourceUtils.getFile("classpath:images\\"+ agency.getPdfHeaderFileName()).getAbsolutePath());
			int indentation = 0;
			float scaler = ((document.getPageSize().getWidth() - indentation) / image.getWidth()) * 100;
			image.scalePercent(scaler);
			image.setAbsolutePosition(0, document.getPageSize().getHeight() + document.topMargin() - image.getHeight());
			document.add(image);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds the header and the footer.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {

		Image image;
		Font fontStyle = new Font();
		fontStyle.setColor(255, 255, 255);
		fontStyle.setSize(10);
		try {
			image = Image.getInstance(ResourceUtils.getFile("classpath:images\\"+ agency.getPdfFooterFileName()).getAbsolutePath());
			int indentation = 0;
			float scaler = ((document.getPageSize().getWidth() - indentation) / image.getWidth()) * 100;
			image.scalePercent(scaler);
			image.setAbsolutePosition(0, 0);
			document.add(image);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		String domainName = "from " + agency.getDomainName();
		ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
				new Phrase(String.format("Page - %d, Printed on : %s %s", pagenumber, date, domainName), fontStyle),
				(document.getPageSize().getWidth()) / 2, document.bottomMargin() - 25, 0);
	}
}