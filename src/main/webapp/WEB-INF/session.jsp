<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>People Page</title>
</head>
<body>
    <c:forEach items="${errors}" var="error">
        <c:out value=${error} />
    </c:forEach>
</body>


