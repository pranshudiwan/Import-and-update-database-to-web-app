import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Servlet implementation class ImportDatabase
 */
@WebServlet("/ImportDatabase")
public class ImportDatabase extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head>" + "<link rel = 'stylesheet' type = 'text/css' href = 'style.css'>" + "</head>"
				+ "<body>" + "<center><br><br><br><br><h2>Importing specified database...</h2>"
						+ "<nav class='main-nav'><a href='index.jsp'>Import</a>"
								+ "<a href='main.jsp'>Search / Update / Delete</a>"
										+ "<a href='EmployeeController?action=insert'>Insert</a>"
										+ "<a href='EmployeeController?action=listEmployees'>Insert</a></nav>");
		PreparedStatement preparedstatement = null;
		Connection connection = null;
		int resultset = 0;
		String name = null;
		String dept = null;
		Statement statement = null;
		ResultSet resultSet = null;
		int compare_id = 0;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/", "SA", "");
			
			/* parse the external database (.csv file) row wise */
			
			String DatabaseName = (String)request.getAttribute("attributeName");
			Scanner input_database = new Scanner(new File(DatabaseName));
			out.println("<div class = 'box1'><table><tr><th>Event</th><th>Action</th></tr>");
			
			while (input_database.hasNextLine()) {
				String line = input_database.nextLine();
				StringTokenizer stringtokenizer = new StringTokenizer(line, ",");
				while (stringtokenizer.hasMoreElements()) {
					int id = Integer.parseInt(stringtokenizer.nextToken());
					name = stringtokenizer.nextToken();
					float mobile = Float.parseFloat(stringtokenizer.nextToken());
					dept = stringtokenizer.nextToken();
					String sql = "SELECT id from hexaware_employees where id = '" + id + "' ";
					statement = connection.createStatement();
					resultSet = statement.executeQuery(sql);
					while (resultSet.next()) {
						compare_id = resultSet.getInt("id");
					}
					/*
					 * check if record exists in database by comparing id in
					 * .csv file and id present in database if ID exists, then
					 * update record in database, else insert record in database
					 */
					if (id == compare_id) {
						out.println("<tr><td>" + id + " exists in database..." + "Updating row..." + "</td>");
						String sql2 = "Update hexaware_employees SET name = ?, mobile = ?, dept = ? where id = '" + id
								+ "' ";
						preparedstatement = connection.prepareStatement(sql2);
						preparedstatement.setString(3, dept);
						preparedstatement.setString(1, name);
						preparedstatement.setFloat(2, mobile);
						resultset = preparedstatement.executeUpdate();
						out.println("<td>" + "1 record updated successfully! " + "</td></tr>");
					} else {
						out.println("<tr><td>Employee does not exist in database..." + "Inserting row..." + "</td>");
						String sql3 = "INSERT INTO hexaware_employees(name, mobile, dept)" + "values(?,?,?);";
						preparedstatement = connection.prepareStatement(sql3);
						preparedstatement.setString(3, dept);
						preparedstatement.setString(1, name);
						preparedstatement.setFloat(2, mobile);
						resultset = preparedstatement.executeUpdate();
						out.println("<td>" + "1 record inserted successfully!" + "</td></tr>");
					}				
				}
			}
			out.println(
					"</table><br><h4 style = 'font-family:sans-serif;'>Database imported successfully! You are being redirected to the search page... </h4></div>");
			response.setHeader("Refresh", "8; URL=main.jsp");
		} catch (ClassNotFoundException e) {
			out.println("Oops...Class not found! \n");
		} catch (SQLException e) {
			out.println("Oops...SQL Excepion! \n");
		} catch (FileNotFoundException e) {
			out.println("Oops...File not found!" + "<br><br>" + "<form action = 'main.jsp' method = 'POST' > "
					+ "<input type = 'submit' value = 'Go back'> </form>"
					+ "<form action = 'index.htm' method = 'POST' > "
					+ "<input type = 'submit' value = 'Search existing database'></form>");
		}
	}
}
