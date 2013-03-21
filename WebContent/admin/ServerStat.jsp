<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page  import="com.shntec.bp.util.DatabaseManager" %>
<%
	String flag = request.getParameter("flag");
	if (flag!= null && flag.equals("1")) {
		// Reset database
		DatabaseManager.getInstance().resetDS();
		return;
	}
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title> Server Statistics </title>
</head>
<body>
  <script> 
  
  function reset_database(){
    
	var orig = self.location.pathname;
	var request = orig + "?flag=1";
    
	var xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange=function() {
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				alert("所有数据库连接已经被重置。");
				self.location= orig;
			}
			else {
				alert("执行服务器操作错误，服务器响应状态码(response status)：" + xmlhttp.status);
			}
		}
	}
	xmlhttp.open("GET", request, true);
	xmlhttp.send();
  }
  
  </script>

  <div>
    <span> 当前数据库连接 ：</span> 
    <span> <%= DatabaseManager.getInstance().getCurrentConnectionCount() %> </span>
    <input style="margin-left:50px;" type="button" value="重置所有数据库连接" onclick="reset_database()" />
  </div>
</body>
</html>