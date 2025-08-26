# 🔍 Search Algorithm Performance Comparison (Java)
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.oracle.com/java/)
[![JFreeChart](https://img.shields.io/badge/Charting-JFreeChart-blue)](http://www.jfree.org/jfreechart/)
[![Data](https://img.shields.io/badge/Dataset-20K%20Articles-green)](src/main/resources/Article.csv)

This project **implements and compares four different search algorithms** on academic article data, analyzing their **time complexity** across **ArrayList** and **LinkedList** data structures with comprehensive performance visualization.

---

## 🚀 Features

- **Linear Search** implementation (**O(n)**) - Sequential scanning
- **Binary Search** implementation (**O(log n)**) - Divide and conquer
- **Jump Search** implementation (**O(√n)**) - Block jumping optimization  
- **Exponential Search** implementation (**O(log n)**) - Range doubling + binary search
- CPU execution time measurement using `System.nanoTime()`
- **Interactive CLI** for individual article searches
- **Automated performance racing** (30 runs per algorithm)
- **Multi-threaded benchmarking** for accurate timing
- Performance visualization with **5 different chart types**
- **ArrayList vs LinkedList** comparison analysis
- Processes **20,972 real academic articles** from CSV

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
### Method 1: GitHub
Download the project as a ZIP, unzip the folder, and run:
 - Main.java file for analysis
 - Graphs.java for visual representations

### Method 2
### 1. Clone repository
```bash
git clone <https://github.com/anishh-fiji/CS214-Assignment-1>
cd search-algorithms-assignment
```

### 2. Compile
```bash
javac -cp ".:jfreechart-*.jar" *.java
```

### 3. Run
```bash
java -cp ".:jfreechart-*.jar" Main
```
---

- Interactive menu with 4 options appears
- Choose individual search, algorithm race, or visualizations
- Multiple performance charts will be generated

---

## 📊 Example Output (Console)

```
Reading CSV file: src\main\resources\Article.csv
Successfully loaded articles: 20972

=== RACE RESULTS ===
Algorithm                 Best(ms) Worst(ms) Mean(ms) Found    Runs    
======================================================================
Exponential - ArrayList   0.008    0.970    0.053    15       30
Jump - ArrayList          0.010    0.248    0.054    15       30      
Binary - LinkedList       0.036    0.686    0.111    15       30
Exponential - LinkedList  0.029    0.742    0.144    15       30
Binary - ArrayList        0.004    13.096   0.445    15       30
Jump - LinkedList         0.084    5.564    1.720    15       30
Linear - ArrayList        0.050    17.156   3.707    15       30
Linear - LinkedList       0.639    246.615  146.887  15       30

=== PERFORMANCE ANALYSIS ===
LinkedList Performance Analysis:
- Linear: LinkedList is 39.6x slower than ArrayList
- Binary: LinkedList is 0.3x slower than ArrayList  
- Jump: LinkedList is 32.0x slower than ArrayList
- Exponential: LinkedList is 2.7x slower than ArrayList
```

---

## 📈 Example Visualizations

The program generates **5 different performance charts**:

1. **Race Results** - Mean execution time comparison
2. **Best/Mean/Worst Analysis** - Statistical performance breakdown  
3. **Data Structure Comparison** - ArrayList vs LinkedList head-to-head
4. **Complexity Analysis** - Theoretical vs empirical results
5. **Algorithm Performance Trends** - Detailed performance patterns

**Key Findings:**
- **ArrayList** dominates for random-access algorithms
- **LinkedList** shows exponential performance degradation  
- **Exponential Search** fastest overall on ArrayList
- **Linear Search** on LinkedList slowest by massive margin

---
| Assignment Task                                                                                        | Marks | Code Implementation                                                                                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------------------------ | ----- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1. Implement 4 generic search algorithms for ArrayList & LinkedList**                                | 4%    | `SearchAlgorithms.java` contains **LinearSearch, BinarySearch, JumpSearch, ExponentialSearch**. Each algorithm is **generic**, working with any `Article` object. Implemented to handle both `ArrayList<Article>` and `LinkedList<Article>` in `Main.java`.                  |
| **2. Race all algorithms on random test cases simultaneously**                                         | 5%    | `Main.java` contains the **algorithm race logic** using **multi-threading** (`ExecutorService`) to benchmark all 8 combinations (4 algorithms × 2 data structures). Results are stored in `Main$AlgorithmStats`.                                                             |
| **3. Run 30 trials with random keys, including missing keys, and analyze best/mean/worst performance** | 5%    | The **race feature in `Main.java`** runs each algorithm **30 times** with random keys (some missing). Uses counters to track **best, mean, and worst execution times**. Analysis is printed in console tables and stored for visualization.                                  |
| **4. Determine worst-case time complexity graphically**                                                | 6%    | **Visualization module** (via JFreeChart or MATLAB commands in Java) generates **5 charts**, including complexity analysis, data structure comparison, and theoretical vs empirical trends. Relevant methods: `Main.generateCharts()`, `ChartBuilder.java` (if implemented). |

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
   - Visual complexity analysis
   - Empirical vs theoretical comparison

4. **Exit Program**

---

## 📚 Complexity Analysis

| Algorithm | Time Complexity | Best For | Notes |
|-----------|----------------|----------|-------|
| **Linear Search** | **O(n)** | Unsorted data, LinkedList | Sequential scan |
| **Binary Search** | **O(log n)** | Large sorted ArrayList | Requires sorted input |
| **Jump Search** | **O(√n)** | Very large sorted ArrayList | Optimal jump size √n |
| **Exponential Search** | **O(log n)** | Unbounded sorted ArrayList | Best when target near start |

### Performance Insights:
- **ArrayList**: Constant-time random access enables efficient divide-and-conquer
- **LinkedList**: O(n) random access destroys efficiency of advanced algorithms  
- **Data Structure Choice**: Critical performance factor (up to 39.6x difference)

---

## 🎯 Key Performance Results

**Fastest Combinations:**
- Exponential Search + ArrayList: **0.053ms mean**
- Jump Search + ArrayList: **0.054ms mean**  
- Binary Search + LinkedList: **0.111ms mean**

**Slowest Combinations:**  
- Linear Search + LinkedList: **146.887ms mean**
- Jump Search + LinkedList: **1.720ms mean**
- Linear Search + ArrayList: **3.707ms mean**

Winner: Exponential Search on ArrayList (fastest overall)

---

## ⚡ Technical Highlights

- **Multi-threaded Performance Testing** using ExecutorService
- **Robust CSV Processing** with encoding support and error handling
- **Memory Efficient** streaming approach for large datasets
- **Thread-Safe Statistics** using ConcurrentHashMap
- **Comprehensive Data Validation** with format checking
- Real-World Dataset: Of 20,972+ academic articles

---

## ✨ Authors
- 👨‍💻 Aryan Singh (Chief Programmer)
- 👨‍💻 Mohammed Suhail (Testing & Graphical Analyst)
