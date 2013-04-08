import java.util.*;
import java.io.*;
public class DuplicateFinder implements Runnable
{
	FileInfo f[];
	File dir[];
	int ftop;
	int dtop;
	int lim = 1000000;
	Thread t;
	String txt_update = "";
	public DuplicateFinder()
	{
		ftop = -1;
		dtop = -1;
		f = new FileInfo[lim];
		dir = new File[lim];
		t = new Thread(this,"DupFin");
	}
	public void run()
	{
		try{
		startup();
		}catch(Exception e)
		{
			System.out.println("Error!!");
		}
	}
	public FileInfo analyze(File fl)
	{
		int lm = fl.getName().lastIndexOf('.');
		if(lm==-1) lm = fl.getName().length()-1;
		return new FileInfo( fl.getName().substring(0,lm), fl.getName().substring(lm,fl.getName().length()), fl.getAbsolutePath(), fl.lastModified(), fl.length() );
	}
	public int init_dir(String str)
	{
		File ff = new File(str);
		if(ff.exists() && ff.isDirectory())
			dir[++dtop] = ff;
		else if(ff.exists() && ff.isFile())
			f[++ftop] = analyze(ff);
		else return -1;
		return 1;
	}
	void startup() throws Exception
	{
		String tmp[];
		File t;
		File ftemp;
		txt_update = "Analyzing Directory:";
		for(;;)
		{
			t = dir[dtop--];
			tmp = t.list();
			try
			{
				for(int i=0;i<tmp.length;i++)
				{
					ftemp = new File(t.getAbsolutePath() + "\\" + tmp[i]);
					if(ftemp.exists() && ftemp.isFile())
						f[++ftop] = analyze(ftemp);
					else if(ftemp.exists() && ftemp.isDirectory())
						dir[++dtop] = ftemp;
				}
			}
			catch(Exception e)
			{
			}
			if(dtop<0)
				break;
		}
		sort();
		txt_update = "end";
	}
	String match() throws Exception
	{
		BufferedWriter nslm = new BufferedWriter(new FileWriter(new File("NameSizeAndModifiedMatches"),false));
		long sz=0;		
		for(int i=0;i<ftop;i++)
		{
			int j;
			for(j=0; (j+i)<ftop && f[j+i].name.equalsIgnoreCase(f[j+i+1].name) && f[j+i].ext.equalsIgnoreCase(f[j+i+1].ext) && f[j+i].length == f[j+i+1].length && f[j+i].lastmodified == f[j+i+1].lastmodified ; j++);
			if(j>0)
			{
				FileInfo arr[] = new FileInfo[j+1];
				for(j=0;(j+i)<ftop && f[j+i].name.equalsIgnoreCase(f[j+i+1].name) && f[j+i].ext.equalsIgnoreCase(f[j+i+1].ext) && f[j+i].length == f[j+i+1].length && f[j+i].lastmodified == f[j+i+1].lastmodified ; j++)
					arr[j] = f[j+i];
				arr[j] = f[j+i];
				int k;
				for(k=0;k<arr.length;k++)
				{
					sz+=arr[k].length;
					nslm.write(arr[k].name + arr[k].ext + ">>" + arr[k].abspath + ">>" + arr[k].length + ">>" + arr[k].lastmodified + "\n");
				}
				sz-=arr[k-1].length;
				nslm.write("\n");
				i=i+j;
			}
		}
		String str = "";
		
		long bt = sz;
		long kbt = bt/1024;
		bt%=1024;
		if(bt>0) str = bt + " bytes";
		long mbt = kbt/1024;
		kbt%=1024;
		if(kbt>0) str = kbt + " Kb " + str;
		long gbt = mbt/1024;
		mbt%=1024;
		if(mbt>0) str = mbt + " Mb " + str;
		if(gbt>0) str = gbt + " Gb " + str;
		
		str += " (" +sz+ " bytes)"; 

		nslm.close();
		
		return ("Total size that could be freed: " + str);
	}
	void sort() throws Exception
	{
		java.util.Comparator c = new ComparePaths();
		java.util.Arrays.sort(f, 0, ftop + 1,c);
	}
}