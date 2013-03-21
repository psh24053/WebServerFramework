<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="java.util.LinkedList"%>
<%@page import="java.util.ArrayList"%>

<%@page import="com.shntec.bp.common.ShntecBaseAction"%>
<%@page import="com.shntec.bp.common.ShntecActionHandler"%>


<% 
	LinkedList<ShntecBaseAction> currentActionList = 
		ShntecActionHandler.getInstance().getActionList();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Action Status</title>
</head>
<body>
	<p> Total action count:  <%= currentActionList.size() %></p>
	<table style="" border="1" cellpadding="20">
		<tr>
			<td style="width:200px;" align="center">Action Code</td>
			<td style="width:200px;"  align="center">Action Name</td>
		</tr>
		<% for (int i=0; i<currentActionList.size(); ++i) { %>
		<%     ShntecBaseAction action = currentActionList.get(i); %>
			<tr>
				<td> <%= action.getActionCode() %> </td>
				<td> <%= action.getActionName() %> </td>
			</tr>
		<% } %>
	</table>
</body>
</html>