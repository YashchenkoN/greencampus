<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html class="full" lang="en">
<head>
    <title>home</title>
    <%@ include file="fragment/head.jspf" %>
    <script src="/resources/js/userREAD.js"></script>
    <link rel="stylesheet" href="/resources/css/test/styles.css">
</head>

<body>
<%@ include file="fragment/header.jspf" %>

<input type="hidden" id="userid" value="${userid}">
<div id="user" class="container">
    <div class="jumbotron col-lg-6 col-md-6 col-sm-offset-3">
        <div class="row">
            <div class="user-avatar">
                <img src="http://www.localevolutionmedia.com/wp-content/uploads/2015/02/ncf_userpic1.png" alt="avatar" id="avatar">
            </div>
        </div>
        <span class="label label-info">Name</span>
        <div class="well well-sm" id="Name"></div>
        <span class="label label-info">Mail</span>
        <div class="well well-sm" id="Mail"></div>
        <div class="pull-right">
            <a href="/user/update" type="button" class="btn btn-info">Update profile</a>
        </div>
    </div>
</div>

</body>

</html>

