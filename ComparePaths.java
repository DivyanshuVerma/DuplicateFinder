public class ComparePaths implements java.util.Comparator
{
	public int compare(Object a, Object b)
	{
		FileInfo y = (FileInfo) a;
		FileInfo z = (FileInfo) b;
		return ((y.name).compareToIgnoreCase(z.name));
	}
}