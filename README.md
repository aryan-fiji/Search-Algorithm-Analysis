# 🔍 Academic Article Search Benchmark (Java)

[![Java](https://img.shields.io/badge/Java-8%2B-orange?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![JFreeChart](https://img.shields.io/badge/Charting-JFreeChart-blue?logo=chart.js&logoColor=white)](http://www.jfree.org/jfreechart/)
[![Dataset](https://img.shields.io/badge/Dataset-20K%20Articles-green?logo=database&logoColor=white)](src/main/resources/Article.csv)
[![License](https://img.shields.io/badge/License-MIT-brightgreen)](LICENSE)

This project **implements and compares four different search algorithms** on academic article data, analyzing their **time complexity** across **ArrayList** and **LinkedList** data structures with comprehensive performance visualization.

---

## 🚀 Features

- 🔹 **4 Generic Search Algorithms**: Linear (O(n)), Binary (O(log n)), Jump (O(√n)), Exponential (O(log n))  
- 🔹 **Flexible Data Structures**: ArrayList vs LinkedList  
- 🔹 **Robust CSV Parsing**:
  - Multi-line support  
  - Quotes & commas inside fields  
  - Handles missing/malformed data gracefully  
- 🔹 **Interactive CLI** for single article search  
- 🔹 **Algorithm Race Mode**:
  - Multi-threaded benchmarking for all 8 algorithm/data structure combos  
  - 30 random runs, including missing keys  
  - Tracks Best / Mean / Worst execution times  
- 🔹 **Automated Charts**: 5 types visualizing algorithm trends, data structure comparison, and complexity analysis  
- 🔹 **Data Insights**: Category counts, missing fields, ID validation, performance logs  

---

## 📂 Project Structure

```
.
├── .idea/                                  # IntelliJ IDEA configuration files
│   ├── .gitignore
│   ├── compiler.xml
│   ├── jarRepositories.xml
│   ├── misc.xml
│   └── vcs.xml
├── .vscode/                                # VS Code configuration
│   └── settings.json
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Article.java                # Article data model with comparison methods
│   │   │   ├── CSVReader.java              # Robust CSV parsing with error handling
│   │   │   ├── Main.java                   # Main program with CLI and performance testing
│   │   │   └── SearchAlgorithms.java       # All four search algorithm implementations
│   │   └── resources/
│   │       └── Article.csv                 # Dataset with 20,972+ academic articles
│   └── test/
├── target/                                 # Compiled classes and build artifacts
│   ├── classes/
│   │   ├── Article.class
│   │   ├── Article.csv
│   │   ├── CSVReader.class
│   │   ├── Main.class
│   │   ├── Main$AlgorithmConfig.class
│   │   ├── Main$AlgorithmStats.class
│   │   ├── Main$SearchFunction.class
│   │   └── SearchAlgorithms.class
│   └── test-classes/
│       └── TestAlgorithmRacePart2.class
├── pom.xml                                 # Maven configuration file
└── README.md                               # Project documentation
```

---

## 🛠️ Setup & Run

### 💻 Local Project
```bash
javac -cp ".:jfreechart-*.jar" *.java
java -cp ".:jfreechart-*.jar" Main
```

**CLI Options:**
1. Search for an article using ID 
2. Race all search algorithms (30 runs - Random Values)  
3. Generate performance charts  
4. Show theoretical complexity curves  
5. End program  

✅ Ensure that `Article.csv` is in `src/main/resources/`.

### 🌐 GitHub
```bash
git clone https://github.com/aryan-fiji/CS214-Assignment-1
cd search-algorithms-assignment
javac -cp ".:jfreechart-*.jar" *.java
java -cp ".:jfreechart-*.jar" Main
```

---

## 📊 Example Output

```
Reading CSV file: src\main\resources\Article.csv
Successfully loaded articles: 20972

=== RACE RESULTS ===
Algorithm                 Best(ms) Worst(ms) Mean(ms) Found    Runs    
======================================================================
Exponential - ArrayList   0.008    0.970    0.053    15       30
Jump - ArrayList          0.010    0.248    0.054    15       30      
Binary - LinkedList       0.036    0.686    0.111    15       30
Linear - LinkedList       0.639    246.615  146.887  15       30
...
```

**Insights**:  
- ⚡ ArrayList dominates random-access algorithms  
- 🐢 LinkedList suffers severe slowdowns  
- 🏆 Exponential Search on ArrayList is fastest  

---

## 📈 Performance Charts

- 🏁 **Race Results** – Mean execution time comparison  
- 📊 **Best/Mean/Worst Analysis** – Statistical performance breakdown  
- 🔀 **Data Structure Comparison** – ArrayList vs LinkedList  
- 📉 **Complexity Analysis** – Theoretical vs empirical  
- 📈 **Algorithm Performance Trends** – Multi-run evaluation  

---

## 🔧 Assignment Mapping

| Task | Marks | Implementation |
|------|-------|----------------|
| Implement 4 generic search algorithms | 4% | `SearchAlgorithms.java` – works with any `Article`, ArrayList & LinkedList |
| Race all algorithms | 5% | `Main.java` – multi-threaded 8-combo benchmarking |
| Run 30 trials & analyze performance | 5% | Race mode tracks best, mean, worst; includes missing keys |
| Worst-case complexity visualization | 6% | `Main.generateCharts()` – 5 charts including theoretical vs empirical |

---

## 🔧 Interactive Features

### Menu Options:
1. **Individual Article Search**
   - Enter article ID to search for
   - Choose from 4 algorithms  
   - Select ArrayList or LinkedList
   - View detailed timing and results

2. **Algorithm Race (30 runs)**
   - Automated testing of all combinations
   - Statistical analysis with best/worst/mean times
   - Performance recommendations

3. **Performance Visualizations** 
   - Generate all 5 chart types
   - Empirical vs theoretical comparison

4. **Complexity Graphs** 
   - Visual complexity analysis
     
5. **Exit Program**

---

## 🎯 Key Performance Results

**Fastest**:  
- 🏎 Exponential Search + ArrayList: 0.053ms mean  
- ⚡ Jump Search + ArrayList: 0.054ms mean  

**Slowest**:  
- 🐢 Linear Search + LinkedList: 146.887ms mean  

---

## ⚡ Technical Highlights

- ⚙ Multi-threaded benchmarking (`ExecutorService`)  
- 📄 Robust CSV Reader (multi-line, quoted, malformed records)  
- 💾 Memory-efficient streaming for large datasets  
- 🔒 Thread-safe statistics (`ConcurrentHashMap`)  
- ✅ Real-world dataset: 20,972+ academic articles  

---

## ✨ Authors

- 👨‍💻 Aryan Singh – Chief Programmer  
- 👨‍💻 Mohammed Suhail – Testing & Visualization

