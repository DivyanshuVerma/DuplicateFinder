public class FileInfo
{
	String name;
	String ext;
	String abspath;
	long lastmodified;
	long length;
	FileInfo(String a, String b, String c, long d, long e)
	{
		name = a;
		ext = b;
		abspath = c;
		lastmodified = d;
		length = e;
	}
}