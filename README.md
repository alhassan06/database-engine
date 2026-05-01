# Database Engine

A relational database engine built from scratch in Java as part of the CSEN604 – Databases II course at the German University in Cairo.

## Features

- **Page-based storage** — records are stored in fixed-size pages serialized to disk
- **CRUD operations** — create tables, insert records, and query with multiple select modes
- **Multi-column AND selection** — filter records by matching conditions across multiple columns
- **Bitmap Index** — index creation and index-accelerated selection for faster queries
- **Data recovery** — detect and restore missing pages back to their original positions
- **Trace logging** — every operation is logged with execution time for performance analysis

## Project Structure

```
src/
└── DBMS/
    ├── DBApp.java        # Core engine — all DB operations
    ├── Table.java        # Table metadata and trace log
    ├── Page.java         # Page of records (serializable)
    ├── FileManager.java  # Disk I/O — store/load tables, pages, and indices
    └── BitmapIndex.java  # Bitmap index implementation
```

## How It Works

Tables and their pages are serialized as `.db` files under a `Tables/` directory:

```
Tables/
└── student/
    ├── student.db   ← table metadata
    ├── 0.db         ← page 0
    ├── 1.db         ← page 1
    └── gpa.db       ← bitmap index on 'gpa' column
```

## Usage Example

```java
String[] cols = {"id", "name", "major", "semester", "gpa"};
DBApp.createTable("student", cols);

DBApp.insert("student", new String[]{"1", "Alice", "CS", "5", "0.9"});
DBApp.insert("student", new String[]{"2", "Bob",   "BI", "7", "1.2"});

// Select all
ArrayList<String[]> all = DBApp.select("student");

// Select by condition
ArrayList<String[]> filtered = DBApp.select("student",
    new String[]{"major"}, new String[]{"CS"});

// Create bitmap index and use it
DBApp.createBitMapIndex("student", "major");
ArrayList<String[]> indexed = DBApp.selectIndex("student",
    new String[]{"major"}, new String[]{"CS"});

// View trace
System.out.println(DBApp.getFullTrace("student"));
```

## Tech Stack

- Java (no external DB libraries)
- Java Serialization for disk persistence
- JUnit for unit testing
