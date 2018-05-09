import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/UploadDatabase")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)

public class UploadDatabase extends HttpServlet {
	private static final long serialVersionUID = -434186748052700849L;
	private static final String SAVE_DIR = "uploadFiles";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
		String fileName = null;
		String appPath = request.getServletContext().getRealPath("");
		String savePath = appPath + File.separator + SAVE_DIR;

		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		for (Part part : request.getParts()) {
			fileName = extractFileName(part);
			fileName = new File(fileName).getName();
			part.write(savePath + File.separator + fileName);
		}

		String final_path_name = null;
		final_path_name = savePath + File.separator + fileName;

		request.setAttribute("attributeName", final_path_name);
		request.setAttribute("att", fileName);
		RequestDispatcher rd = request.getRequestDispatcher("ImportDatabase");
		rd.forward(request, response);
		} catch (FileNotFoundException e) {
			System.out.println("Oops..File not found!");
		}	
	}

	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}
}
