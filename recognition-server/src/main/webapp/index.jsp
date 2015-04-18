<html>
<body>
	<h2>Speech Recognition RESTful Web Application!</h2>
	<p>
		Visit <a href="http://jersey.java.net">Project Jersey website</a> for
		more information on Jersey!
	</p>

	<p>This is a test page.</p>
	<form action="api/v1/recognize/upload" method="post"
		enctype="multipart/form-data">

		<p>
			Select a file : <input type="file" name="file" size="45" />
		</p>
		<input type="text" name="name" size="45" />

		<input type="submit" value="Upload It" />
	</form>
</body>
</html>
