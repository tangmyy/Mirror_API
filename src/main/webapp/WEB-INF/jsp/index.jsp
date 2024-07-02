<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Images</title>
</head>
<body>
<h1>Images</h1>
<ul>
    <li>${image.id}: ${image.imgpath}</li>
    <c:forEach items="${images}" var="image">
        <li>${image.id}: ${image.imgpath}</li>
    </c:forEach>
</ul>
</body>
</html>
