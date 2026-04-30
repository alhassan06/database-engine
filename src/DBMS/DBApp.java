package DBMS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


import org.junit.Test;

public class DBApp
{
	static int dataPageSize = 2;
	
	
	public static void createTable(String tableName, String[] columnsNames)
	{
		Table z=new Table (tableName,columnsNames);
		z.traceLog.add("Table created name:"+ tableName+", columnNames:"+Arrays.toString(columnsNames));
		FileManager.storeTable(tableName, z);
		
	}
	
	public static void insert(String tableName, String[] record)
	{
		long start = System.currentTimeMillis();
		int page=0;
		Table z=FileManager.loadTable(tableName);
		if (z.pagecount==0) {
			Page p = new Page ();
			p.records.add(record);
			FileManager.storeTablePage(tableName, z.pagecount, p);
			z.pagecount++;
			page=0;
		}
		else {
		Page p=	FileManager.loadTablePage(tableName, z.pagecount-1);
		if (p.records.size()<dataPageSize) {
			p.records.add(record);
			FileManager.storeTablePage(tableName, z.pagecount - 1, p);
			page=z.pagecount-1;
		}
		else {
			Page x = new Page ();
			x.records.add(record);
			FileManager.storeTablePage(tableName, z.pagecount, x);
			z.pagecount++;
			page=z.pagecount -1;
		}
			
		}
		z.recordCount++;
		long elapsed = System.currentTimeMillis() - start;
		z.traceLog.add("Inserted:" + Arrays.toString(record) 
		    + ", at page number:" + page 
		    + ", execution time (mil):" + elapsed);
		FileManager.storeTable(tableName, z);
	}
	
	public static ArrayList<String []> select(String tableName)
	{
		long start = System.currentTimeMillis();
		Table t=FileManager.loadTable(tableName);
		ArrayList<String[]> result = new ArrayList<>();
		
		for(int i=0; i<t.pagecount;i++) {
			Page p =FileManager.loadTablePage(tableName, i);
			for(int j=0;j<p.records.size();j++) {
				result.add(p.records.get(j));
			}
		}
		long elapsed = System.currentTimeMillis() - start;
		t.traceLog.add("Select all pages:" + t.pagecount 
		    + ", records:" + result.size() 
		    + ", execution time (mil):" + elapsed);
		FileManager.storeTable(tableName, t);
		
		return result;
	}
	
	public static ArrayList<String []> select(String tableName, int pageNumber, int recordNumber)
	{
		long start = System.currentTimeMillis();
		ArrayList<String[]> result = new ArrayList<>();
		Table t=FileManager.loadTable(tableName);
		Page p=FileManager.loadTablePage(tableName, pageNumber);
		if (recordNumber < p.records.size()) {
		    result.add(p.records.get(recordNumber));
		}
		
		long elapsed = System.currentTimeMillis() - start;
		t.traceLog.add("Select pointer page:" + pageNumber
			    + ", record:" + recordNumber
			    + ", total output count:" + result.size()
			    + ", execution time (mil):" + elapsed);
		FileManager.storeTable(tableName, t);
		return result;
	}
	
	public static ArrayList<String[]> select(String tableName, String[] cols, String[] vals){
    long start = System.currentTimeMillis();
    ArrayList<String[]> result = new ArrayList<>();
    Table t = FileManager.loadTable(tableName);
    int[] pageFrequency = new int[t.pagecount];
    
    for (int i = 0; i < t.pagecount; i++) {
        Page p = FileManager.loadTablePage(tableName, i);
        for (int j = 0; j < p.records.size(); j++) {
            String[] record = p.records.get(j);
            boolean match = true;
            
            for (int c = 0; c < cols.length; c++) {
                for (int k = 0; k < t.columnNames.length; k++) {
                    if (t.columnNames[k].equals(cols[c])) {
                        if (!record[k].equals(vals[c])) {
                            match = false;
                        }
                    }
                }
            }
            
            if (match) {
                result.add(record);
                pageFrequency[i]++;
            }
        }
    }
    
    ArrayList<String> pagesCounts = new ArrayList<>();
    for (int i = 0; i < pageFrequency.length; i++) {
        if (pageFrequency[i] != 0)
            pagesCounts.add("[" + i + ", " + pageFrequency[i] + "]");
    }
    
    long elapsed = System.currentTimeMillis() - start;
    t.traceLog.add("Select condition:" + Arrays.toString(cols)
        + "->" + Arrays.toString(vals)
        + ", Records per page:" + pagesCounts.toString()
        + ", records:" + result.size()
        + ", execution time (mil):" + elapsed);
    FileManager.storeTable(tableName, t);
    return result;
	}
	
	public static String getFullTrace(String tableName)
	{
		Table t =FileManager.loadTable(tableName);
		String result="";
		for(int i=0;i<t.traceLog.size();i++) {
			result+=t.traceLog.get(i) +"\n";
		}
		result+="Pages Count: " + t.pagecount +", Records Count: "+ t.recordCount;
		return result;
	}
	
	public static String getLastTrace(String tableName)
	{
		Table z=FileManager.loadTable(tableName);
		
		return z.traceLog.get(z.traceLog.size()-1);
	}
	
	
	public static void main(String[] args) throws IOException
	{
	    String[] cols = {"id","name","major","semester","gpa"};
	    createTable("student", cols);
	    String[] r1 = {"1", "stud1", "CS", "5", "0.9"};
	    insert("student", r1);
	    
	    String[] r2 = {"2", "stud2", "BI", "7", "1.2"};
	    insert("student", r2);
	    
	    String[] r3 = {"3", "stud3", "CS", "2", "2.4"};
	    insert("student", r3);
	    
	    String[] r4 = {"4", "stud4", "DMET", "9", "1.2"};
	    insert("student", r4);
	    
	    String[] r5 = {"5", "stud5", "BI", "4", "3.5"};
	    insert("student", r5);

	    System.out.println("Output of selecting the whole table content:");
	    	ArrayList<String[]> result1 = select("student");
	    for (String[] array : result1) {
	        for (String str : array) {
	            System.out.print(str + " ");
	        }
	        System.out.println();
	    }

	    System.out.println("--------------------------------");
	    System.out.println("Output of selecting the output by position:");
	    ArrayList<String[]> result2 = select("student", 1, 1);
	    for (String[] array : result2) {
	        for (String str : array) {
	            System.out.print(str + " ");
	        }
	        System.out.println();
	    }

	    System.out.println("--------------------------------");
	    System.out.println("Output of selecting the output by column condition:");
	    ArrayList<String[]> result3 = select("student", new String[]{"gpa"}, new String[]{"1.2"});
	    for (String[] array : result3) {
	        for (String str : array) {
	            System.out.print(str + " ");
	        }
	        System.out.println();
	    }


	    System.out.println("--------------------------------");
	    System.out.println("Full Trace of the table:");
	    System.out.println(getFullTrace("student"));
	    System.out.println("--------------------------------");
	    System.out.println("Last Trace of the table:");
	    System.out.println(getLastTrace("student"));
	    System.out.println("--------------------------------");
	    System.out.println("The trace of the Tables Folder:");
	    System.out.println(FileManager.trace());
	    FileManager.reset();
	    System.out.println("--------------------------------");
	    System.out.println("The trace of the Tables Folder after resetting:");
	    System.out.println(FileManager.trace());
	}
	
	
	
}
