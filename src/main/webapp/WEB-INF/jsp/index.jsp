<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home Page</title>
</head>
<body>
<h1>Images</h1>
<c:forEach var="image" items="${images}">
    <p>${image.imgpath}</p> <!-- Debugging line -->
    <img src="${pageContext.request.contextPath}/${image.imgpath}" alt="Image" />
</c:forEach>
</body>
</html>
