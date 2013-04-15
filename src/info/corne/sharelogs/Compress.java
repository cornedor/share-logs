package info.corne.sharelogs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress {
	private static final int BUFFER = 20;
	
	private String[] files;
	private String zip;
	
	public Compress(String[] files, String zip)
	{
		this.files = files;
		this.zip = zip;
	}
	
	public void compress()
	{
		try
		{
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zip);
			ZipOutputStream out = new ZipOutputStream(dest);
			byte data[] = new byte[BUFFER];
			for(int i = 0; i < files.length; i++)
			{
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1)
				{
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
