package DBMS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Table implements Serializable
{
	String tableName;
	String [] columnNames;
	int pagecount;
	int recordCount;
	ArrayList<String> traceLog;
	
 public Table(String tableName ,String [] columnNames) {
	 this.tableName= tableName;
	 this.columnNames=columnNames;
	 this.pagecount=0;
	 this.recordCount=0;
	 this.traceLog=new ArrayList<>();
 }
}
