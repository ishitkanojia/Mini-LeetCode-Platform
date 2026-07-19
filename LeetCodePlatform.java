import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

// ============================================================
//  Mini LeetCode Platform  —  Single-File Java Swing App
//  DSA Used: HashMap, Trie, PriorityQueue (Heap), Queue,
//            Stack, Graph (adjacency list), Sorting
// ============================================================

public class LeetCodePlatform {

    // ── Colour Palette ──────────────────────────────────────
    static final Color BG_DARK      = new Color(13, 17, 23);
    static final Color BG_PANEL     = new Color(22, 27, 34);
    static final Color BG_CARD      = new Color(30, 36, 46);
    static final Color ACCENT       = new Color(88, 166, 255);
    static final Color ACCENT2      = new Color(63, 185, 80);
    static final Color ACCENT_WARN  = new Color(210, 153, 34);
    static final Color ACCENT_RED   = new Color(248, 81, 73);
    static final Color TEXT_PRIMARY = new Color(230, 237, 243);
    static final Color TEXT_MUTED   = new Color(125, 133, 144);
    static final Color BORDER       = new Color(48, 54, 61);

    // ── Fonts ────────────────────────────────────────────────
    static Font FONT_MONO;
    static Font FONT_BOLD;
    static Font FONT_BODY;

    static {
        FONT_MONO = new Font("Monospaced", Font.PLAIN, 13);
        FONT_BOLD = new Font("SansSerif",  Font.BOLD,  14);
        FONT_BODY = new Font("SansSerif",  Font.PLAIN, 13);
    }

    // ════════════════════════════════════════════════════════
    //  DSA: Trie  (auto-suggest problem titles)
    // ════════════════════════════════════════════════════════
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
        String fullWord = "";
    }

    static class Trie {
        private final TrieNode root = new TrieNode();

        void insert(String word) {
            TrieNode cur = root;
            for (char c : word.toLowerCase().toCharArray()) {
                cur.children.putIfAbsent(c, new TrieNode());
                cur = cur.children.get(c);
            }
            cur.isEnd = true;
            cur.fullWord = word;
        }

        List<String> suggest(String prefix) {
            List<String> result = new ArrayList<>();
            TrieNode cur = root;
            for (char c : prefix.toLowerCase().toCharArray()) {
                if (!cur.children.containsKey(c)) return result;
                cur = cur.children.get(c);
            }
            dfs(cur, result);
            return result;
        }

        private void dfs(TrieNode node, List<String> result) {
            if (result.size() >= 5) return;
            if (node.isEnd) result.add(node.fullWord);
            for (TrieNode child : node.children.values()) dfs(child, result);
        }
    }

    // ════════════════════════════════════════════════════════
    //  DSA: Problem model + difficulty enum
    // ════════════════════════════════════════════════════════
    enum Difficulty { EASY, MEDIUM, HARD }
    enum Tag { ARRAY, STRING, GRAPH, TREE, DP, HASH, STACK, QUEUE, SORTING, GREEDY }

    static class Problem {
        int id; String title, description, sampleInput, sampleOutput, hint;
        Difficulty difficulty; Tag tag;

        Problem(int id, String title, String description,
                String sampleInput, String sampleOutput, String hint,
                Difficulty difficulty, Tag tag) {
            this.id = id; this.title = title; this.description = description;
            this.sampleInput = sampleInput; this.sampleOutput = sampleOutput;
            this.hint = hint; this.difficulty = difficulty; this.tag = tag;
        }
    }

    // ════════════════════════════════════════════════════════
    //  DSA: Submission + Queue for pending judge
    // ════════════════════════════════════════════════════════
    enum Verdict { ACCEPTED, WRONG_ANSWER, TIME_LIMIT, COMPILE_ERROR }

    static class Submission {
        String username; int problemId; long timestamp;
        String code; Verdict verdict; int score;
        Submission(String u, int p, String c, Verdict v, int s) {
            username = u; problemId = p; code = c; verdict = v;
            score = s; timestamp = System.currentTimeMillis();
        }
    }

    // ════════════════════════════════════════════════════════
    //  DSA: User + HashMap<username, User>
    // ════════════════════════════════════════════════════════
    static class User implements Comparable<User> {
        String username, password;
        int score, solvedCount, streak;
        Set<Integer> solved = new HashSet<>();
        List<Submission> history = new ArrayList<>();

        User(String u, String p) { username = u; password = p; }

        @Override public int compareTo(User o) {
            if (o.score != score) return Integer.compare(o.score, score);
            return Integer.compare(o.solvedCount, solvedCount);
        }
    }

    // ════════════════════════════════════════════════════════
    //  DSA: Leaderboard backed by PriorityQueue (Max-Heap)
    // ════════════════════════════════════════════════════════
    static class Leaderboard {
        private final Map<String, User> userRef;
        Leaderboard(Map<String, User> userRef) { this.userRef = userRef; }

        List<User> getTop(int n) {
            PriorityQueue<User> heap = new PriorityQueue<>(userRef.values());
            List<User> result = new ArrayList<>();
            while (!heap.isEmpty() && result.size() < n) result.add(heap.poll());
            return result;
        }
    }

    // ════════════════════════════════════════════════════════
    //  DSA: Problem Graph — tag-based adjacency (related problems)
    // ════════════════════════════════════════════════════════
    static class ProblemGraph {
        Map<Integer, List<Integer>> adj = new HashMap<>();

        void addEdge(int a, int b) {
            adj.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            adj.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }

        // BFS to find related problems
        List<Integer> related(int start, int limit) {
            List<Integer> result = new ArrayList<>();
            if (!adj.containsKey(start)) return result;
            Queue<Integer> queue = new LinkedList<>();
            Set<Integer> visited = new HashSet<>();
            queue.add(start); visited.add(start);
            while (!queue.isEmpty() && result.size() < limit) {
                int cur = queue.poll();
                for (int nb : adj.getOrDefault(cur, List.of())) {
                    if (!visited.contains(nb)) {
                        visited.add(nb); queue.add(nb);
                        if (nb != start) result.add(nb);
                    }
                }
            }
            return result;
        }
    }

    // ════════════════════════════════════════════════════════
    //  App State (singleton-style statics)
    // ════════════════════════════════════════════════════════
    static final Map<String, User>    users       = new HashMap<>();
    static final Map<Integer, Problem> problems   = new LinkedHashMap<>();
    static final Trie                 trie        = new Trie();
    static final ProblemGraph         graph       = new ProblemGraph();
    static final Queue<Submission>    judgeQueue  = new LinkedList<>();
    static       User                 currentUser = null;
    static       Leaderboard          leaderboard;
    static       JFrame               mainFrame;
    static       CardLayout           cardLayout  = new CardLayout();
    static       JPanel               rootPanel;

    // ════════════════════════════════════════════════════════
    //  Seed data
    // ════════════════════════════════════════════════════════
    static void seedData() {
        // Problems
        addProblem(1, "Two Sum",
            "Given an array of integers nums and a target integer, return indices of the two numbers that add up to target.\n\nYou may assume that each input has exactly one solution.",
            "nums = [2,7,11,15], target = 9", "Output: [0, 1]",
            "Use a HashMap to store num→index as you iterate.", Difficulty.EASY, Tag.HASH);

        addProblem(2, "Valid Parentheses",
            "Given a string containing just '(', ')', '{', '}', '[', ']', determine if it is valid.\nAn input string is valid if every open bracket is closed by the same type of bracket.",
            "s = \"()[]{}\"", "Output: true",
            "Use a Stack. Push open brackets, pop and match close brackets.", Difficulty.EASY, Tag.STACK);

        addProblem(3, "Binary Search",
            "Given a sorted array of integers and a target value, return the index if found, otherwise return -1.\n\nYou must write an algorithm with O(log n) runtime complexity.",
            "nums = [-1,0,3,5,9,12], target = 9", "Output: 4",
            "Maintain lo, hi pointers. Compare mid with target.", Difficulty.EASY, Tag.ARRAY);

        addProblem(4, "Maximum Subarray",
            "Given an integer array nums, find the subarray with the largest sum, and return its sum.\n\nThis is the classic Kadane's Algorithm problem.",
            "nums = [-2,1,-3,4,-1,2,1,-5,4]", "Output: 6  (subarray [4,-1,2,1])",
            "Track currentSum and maxSum. Reset currentSum when it goes negative.", Difficulty.MEDIUM, Tag.DP);

        addProblem(5, "Number of Islands",
            "Given an m×n 2D binary grid of '1's (land) and '0's (water), return the number of islands.\nAn island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically.",
            "grid = [[1,1,0],[0,1,0],[0,0,1]]", "Output: 2",
            "DFS/BFS from each unvisited '1'. Mark visited cells as '0'.", Difficulty.MEDIUM, Tag.GRAPH);

        addProblem(6, "Longest Common Subsequence",
            "Given two strings text1 and text2, return the length of their longest common subsequence.\nA subsequence is derived by deleting some (or no) characters without changing order.",
            "text1 = \"abcde\", text2 = \"ace\"", "Output: 3  (\"ace\")",
            "Use a 2D DP table. dp[i][j] = LCS of text1[0..i] and text2[0..j].", Difficulty.MEDIUM, Tag.DP);

        addProblem(7, "Course Schedule",
            "There are numCourses courses (0 to n-1). Given prerequisites pairs [a,b] meaning b→a, determine if you can finish all courses.\n\nDetect if a cycle exists in a directed graph.",
            "numCourses = 2, prerequisites = [[1,0]]", "Output: true",
            "Build a directed graph. Use DFS with visited/in-stack arrays to detect cycles.", Difficulty.MEDIUM, Tag.GRAPH);

        addProblem(8, "Word Search",
            "Given an m×n grid of characters and a word, return true if the word exists in the grid.\nThe word must be constructed from adjacent cells (up/down/left/right). No cell reused.",
            "board = [[A,B,C,E],[S,F,C,S],[A,D,E,E]], word = \"ABCCED\"", "Output: true",
            "Backtracking DFS. Mark cell as visited, recurse, then unmark.", Difficulty.HARD, Tag.ARRAY);

        addProblem(9, "Merge K Sorted Lists",
            "You are given an array of k linked-lists, each sorted in ascending order.\nMerge all the linked-lists into one sorted linked-list and return it.",
            "lists = [[1,4,5],[1,3,4],[2,6]]", "Output: [1,1,2,3,4,4,5,6]",
            "Use a Min-Heap (PriorityQueue). Insert heads, poll minimum, add next node.", Difficulty.HARD, Tag.QUEUE);

        addProblem(10, "Trapping Rain Water",
            "Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.",
            "height = [0,1,0,2,1,0,1,3,2,1,2,1]", "Output: 6",
            "Two-pointer approach. Track leftMax and rightMax. Add min(leftMax,rightMax)-height[i].", Difficulty.HARD, Tag.ARRAY);

        addProblem(11, "Reverse Linked List",
            "Given the head of a singly linked list, reverse the list, and return the reversed list.",
            "head = [1,2,3,4,5]", "Output: [5,4,3,2,1]",
            "Use three pointers: prev, curr, next. Iteratively reverse each link.", Difficulty.EASY, Tag.ARRAY);

        addProblem(12, "Climbing Stairs",
            "You are climbing a staircase with n steps. Each time you can climb 1 or 2 steps.\nIn how many distinct ways can you climb to the top?",
            "n = 5", "Output: 8",
            "dp[i] = dp[i-1] + dp[i-2]. It's basically Fibonacci!", Difficulty.EASY, Tag.DP);

        addProblem(13, "Graph BFS Shortest Path",
            "Given an undirected graph and two nodes src and dst, find the shortest path length between them using BFS.",
            "edges = [[0,1],[1,2],[2,3]], src = 0, dst = 3", "Output: 3",
            "BFS processes nodes level by level. Track distance array.", Difficulty.MEDIUM, Tag.GRAPH);

        addProblem(14, "Kth Largest Element",
            "Given an integer array nums and an integer k, return the kth largest element in the array.\nThis is not the kth distinct element but the kth largest in sorted order.",
            "nums = [3,2,1,5,6,4], k = 2", "Output: 5",
            "Use a Min-Heap of size k. The root is the kth largest.", Difficulty.MEDIUM, Tag.SORTING);

        addProblem(15, "Serialize & Deserialize BST",
            "Design an algorithm to serialize and deserialize a Binary Search Tree.\nSerialization is converting a tree to a string; deserialization rebuilds the tree.",
            "root = [2,1,3]", "Output: [2,1,3] (after serialize + deserialize)",
            "Use preorder traversal for serialization. Rebuild using BST properties.", Difficulty.HARD, Tag.TREE);

        // Build Trie
        problems.values().forEach(p -> trie.insert(p.title));

        // Build Problem Graph (connect same-tag problems)
        Map<Tag, List<Integer>> byTag = new EnumMap<>(Tag.class);
        for (Problem p : problems.values())
            byTag.computeIfAbsent(p.tag, k -> new ArrayList<>()).add(p.id);
        for (List<Integer> group : byTag.values())
            for (int i = 0; i < group.size(); i++)
                for (int j = i + 1; j < group.size(); j++)
                    graph.addEdge(group.get(i), group.get(j));

        // Seed users
        User admin = new User("admin", "1234");
        admin.score = 900; admin.solvedCount = 12; admin.streak = 7;
        users.put("admin", admin);
        User alice = new User("alice", "alice");
        alice.score = 650; alice.solvedCount = 8; alice.streak = 3;
        users.put("alice", alice);
        User bob = new User("bob", "bob");
        bob.score = 420; bob.solvedCount = 5; bob.streak = 1;
        users.put("bob", bob);

        leaderboard = new Leaderboard(users);
    }

    static void addProblem(int id, String title, String desc,
                           String input, String output, String hint,
                           Difficulty d, Tag tag) {
        problems.put(id, new Problem(id, title, desc, input, output, hint, d, tag));
    }

    // ════════════════════════════════════════════════════════
    //  Entry Point
    // ════════════════════════════════════════════════════════
    public static void main(String[] args) {
        seedData();
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            buildMainFrame();
        });
    }

    static void buildMainFrame() {
        mainFrame = new JFrame("⚡ LeetCode Platform");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 750);
        mainFrame.setMinimumSize(new Dimension(900, 600));
        mainFrame.setLocationRelativeTo(null);

        rootPanel = new JPanel(cardLayout);
        rootPanel.setBackground(BG_DARK);
        rootPanel.add(buildLoginScreen(),      "LOGIN");
        rootPanel.add(buildRegisterScreen(),   "REGISTER");
        rootPanel.add(buildDashboard(),        "DASHBOARD");

        mainFrame.setContentPane(rootPanel);
        mainFrame.setVisible(true);
        cardLayout.show(rootPanel, "LOGIN");
    }

    // ════════════════════════════════════════════════════════
    //  SCREEN: Login
    // ════════════════════════════════════════════════════════
    static JPanel buildLoginScreen() {
        JPanel outer = darkPanel();
        outer.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_PANEL);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(40, 50, 40, 50)));
        card.setMaximumSize(new Dimension(420, 500));

        JLabel logo = label("⚡", new Font("SansSerif", Font.BOLD, 48), ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = label("LeetCode Platform", new Font("SansSerif", Font.BOLD, 22), TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = label("Practice. Compete. Excel.", FONT_BODY, TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = styledField("Username");
        JPasswordField passField = styledPassField("Password");
        JLabel errLabel = label("", FONT_BODY, ACCENT_RED);
        errLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = accentButton("Sign In", ACCENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton regBtn = ghostButton("Create an account →");
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            User usr = users.get(u);
            if (usr == null || !usr.password.equals(p)) {
                errLabel.setText("Invalid username or password.");
                return;
            }
            currentUser = usr;
            refreshDashboard();
            cardLayout.show(rootPanel, "DASHBOARD");
        });

        // Allow Enter key in fields
        ActionListener loginAction = loginBtn.getActionListeners()[0];
        userField.addActionListener(loginAction);
        passField.addActionListener(loginAction);

        regBtn.addActionListener(e -> cardLayout.show(rootPanel, "REGISTER"));

        // Quick-login hint
        JLabel hint = label("Demo: admin / 1234", FONT_BODY, TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        addGap(card, 10);
        card.add(logo); addGap(card, 6);
        card.add(title); addGap(card, 4);
        card.add(sub); addGap(card, 30);
        card.add(fieldLabel("Username")); addGap(card, 4);
        card.add(userField); addGap(card, 14);
        card.add(fieldLabel("Password")); addGap(card, 4);
        card.add(passField); addGap(card, 8);
        card.add(errLabel); addGap(card, 18);
        card.add(loginBtn); addGap(card, 12);
        card.add(regBtn); addGap(card, 16);
        card.add(hint);

        outer.add(card);
        return outer;
    }

    // ════════════════════════════════════════════════════════
    //  SCREEN: Register
    // ════════════════════════════════════════════════════════
    static JPanel buildRegisterScreen() {
        JPanel outer = darkPanel();
        outer.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_PANEL);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(36, 50, 36, 50)));

        JLabel title = label("Create Account", new Font("SansSerif", Font.BOLD, 20), TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = styledField("Choose a username");
        JPasswordField passField = styledPassField("Password");
        JPasswordField confirmField = styledPassField("Confirm password");
        JLabel errLabel = label("", FONT_BODY, ACCENT_RED);
        errLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createBtn = accentButton("Create Account", ACCENT2);
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton backBtn = ghostButton("← Back to Sign In");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        createBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            String c = new String(confirmField.getPassword());
            if (u.isEmpty() || p.isEmpty()) { errLabel.setText("All fields required."); return; }
            if (!p.equals(c)) { errLabel.setText("Passwords do not match."); return; }
            if (users.containsKey(u)) { errLabel.setText("Username already taken."); return; }
            users.put(u, new User(u, p));
            JOptionPane.showMessageDialog(mainFrame,
                "Account created! You can now sign in.", "Welcome!", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(rootPanel, "LOGIN");
        });

        backBtn.addActionListener(e -> cardLayout.show(rootPanel, "LOGIN"));

        addGap(card, 10);
        card.add(title); addGap(card, 26);
        card.add(fieldLabel("Username")); addGap(card, 4);
        card.add(userField); addGap(card, 14);
        card.add(fieldLabel("Password")); addGap(card, 4);
        card.add(passField); addGap(card, 14);
        card.add(fieldLabel("Confirm Password")); addGap(card, 4);
        card.add(confirmField); addGap(card, 8);
        card.add(errLabel); addGap(card, 18);
        card.add(createBtn); addGap(card, 10);
        card.add(backBtn);

        outer.add(card);
        return outer;
    }

    // ════════════════════════════════════════════════════════
    //  SCREEN: Dashboard (main app after login)
    // ════════════════════════════════════════════════════════
    static JPanel dashboardPanel;
    static JPanel problemListPanel;
    static JPanel leaderboardPanel;
    static JLabel welcomeLabel;

    static JPanel buildDashboard() {
        dashboardPanel = darkPanel();
        dashboardPanel.setLayout(new BorderLayout());

        // Top Nav
        JPanel nav = buildNav();
        dashboardPanel.add(nav, BorderLayout.NORTH);

        // Content area with CardLayout
        JPanel content = new JPanel(new CardLayout());
        content.setBackground(BG_DARK);
        content.add(buildProblemsTab(), "PROBLEMS");
        content.add(buildLeaderboardTab(), "LEADERBOARD");
        content.add(buildProfileTab(), "PROFILE");
        dashboardPanel.add(content, BorderLayout.CENTER);
        dashboardPanel.putClientProperty("contentPanel", content);

        return dashboardPanel;
    }

    static void refreshDashboard() {
        if (welcomeLabel != null && currentUser != null)
            welcomeLabel.setText("Hello, " + currentUser.username + " ✦");
        if (leaderboardPanel != null) refreshLeaderboard();
        if (problemListPanel != null) refreshProblemList("", null);
    }

    // ── Top Navigation Bar ───────────────────────────────────
    static JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(BG_PANEL);
        nav.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(10, 20, 10, 20)));

        JLabel logo = label("⚡ LeetCode Platform", new Font("SansSerif", Font.BOLD, 16), ACCENT);
        welcomeLabel = label("", FONT_BODY, TEXT_MUTED);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tabs.setOpaque(false);

        String[] tabNames = {"Problems", "Leaderboard", "Profile"};
        String[] tabCards = {"PROBLEMS", "LEADERBOARD", "PROFILE"};

        for (int i = 0; i < tabNames.length; i++) {
            final String card = tabCards[i];
            JButton btn = navTabButton(tabNames[i]);
            btn.addActionListener(e -> {
                JPanel content = (JPanel) dashboardPanel.getClientProperty("contentPanel");
                ((CardLayout) content.getLayout()).show(content, card);
                if ("LEADERBOARD".equals(card)) refreshLeaderboard();
            });
            tabs.add(btn);
        }

        JButton logoutBtn = ghostButton("Logout");
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(rootPanel, "LOGIN");
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(welcomeLabel);
        right.add(logoutBtn);

        nav.add(logo, BorderLayout.WEST);
        nav.add(tabs, BorderLayout.CENTER);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ════════════════════════════════════════════════════════
    //  TAB: Problems
    // ════════════════════════════════════════════════════════
    static JPanel buildProblemsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Search + filter bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JTextField searchField = styledField("Search problems...");
        searchField.setPreferredSize(new Dimension(260, 34));

        JComboBox<String> diffFilter = new JComboBox<>(
            new String[]{"All Difficulties", "Easy", "Medium", "Hard"});
        styleCombo(diffFilter);

        JComboBox<String> tagFilter = new JComboBox<>(
            new String[]{"All Topics", "Array", "String", "Graph", "Tree",
                         "DP", "Hash", "Stack", "Queue", "Sorting", "Greedy"});
        styleCombo(tagFilter);

        // Trie-based suggestions popup
        JPopupMenu suggestions = new JPopupMenu();
        suggestions.setBackground(BG_CARD);
        suggestions.setBorder(new LineBorder(BORDER));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                String text = searchField.getText().trim();
                suggestions.removeAll();
                if (!text.isEmpty()) {
                    List<String> s = trie.suggest(text);
                    for (String t : s) {
                        JMenuItem item = new JMenuItem(t);
                        item.setBackground(BG_CARD);
                        item.setForeground(TEXT_PRIMARY);
                        item.setFont(FONT_BODY);
                        item.addActionListener(ev -> {
                            searchField.setText(t);
                            suggestions.setVisible(false);
                            refreshProblemList(t, getFilterDiff(diffFilter), getFilterTag(tagFilter));
                        });
                        suggestions.add(item);
                    }
                    if (!s.isEmpty()) suggestions.show(searchField, 0, searchField.getHeight());
                    else suggestions.setVisible(false);
                } else suggestions.setVisible(false);
                refreshProblemList(text, getFilterDiff(diffFilter), getFilterTag(tagFilter));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        diffFilter.addActionListener(e ->
            refreshProblemList(searchField.getText().trim(),
                getFilterDiff(diffFilter), getFilterTag(tagFilter)));
        tagFilter.addActionListener(e ->
            refreshProblemList(searchField.getText().trim(),
                getFilterDiff(diffFilter), getFilterTag(tagFilter)));

        searchBar.add(searchField);
        searchBar.add(diffFilter);
        searchBar.add(tagFilter);

        // Problem list table
        String[] cols = {"#", "Title", "Difficulty", "Topic", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildProblemTable(model);

        problemListPanel = new JPanel(new BorderLayout());
        problemListPanel.setOpaque(false);
        problemListPanel.putClientProperty("tableModel", model);
        problemListPanel.putClientProperty("table", table);

        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        problemListPanel.add(scroll);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(problemListPanel, BorderLayout.CENTER);

        // Open problem on row double-click
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    int pid = (int) model.getValueAt(row, 0);
                    openProblemEditor(problems.get(pid));
                }
            }
        });

        refreshProblemList("", null, null);
        return panel;
    }

    static Difficulty getFilterDiff(JComboBox<String> cb) {
        return switch (cb.getSelectedIndex()) {
            case 1 -> Difficulty.EASY;
            case 2 -> Difficulty.MEDIUM;
            case 3 -> Difficulty.HARD;
            default -> null;
        };
    }

    static Tag getFilterTag(JComboBox<String> cb) {
        return switch (cb.getSelectedIndex()) {
            case 1 -> Tag.ARRAY; case 2 -> Tag.STRING;
            case 3 -> Tag.GRAPH; case 4 -> Tag.TREE;
            case 5 -> Tag.DP;    case 6 -> Tag.HASH;
            case 7 -> Tag.STACK; case 8 -> Tag.QUEUE;
            case 9 -> Tag.SORTING; case 10 -> Tag.GREEDY;
            default -> null;
        };
    }

    static void refreshProblemList(String query, Difficulty diff) {
        refreshProblemList(query, diff, null);
    }

    static void refreshProblemList(String query, Difficulty diff, Tag tag) {
        if (problemListPanel == null) return;
        DefaultTableModel model = (DefaultTableModel) problemListPanel.getClientProperty("tableModel");
        if (model == null) return;
        model.setRowCount(0);
        for (Problem p : problems.values()) {
            boolean matchQ = query.isEmpty() || p.title.toLowerCase().contains(query.toLowerCase());
            boolean matchD = diff == null || p.difficulty == diff;
            boolean matchT = tag == null || p.tag == tag;
            if (!matchQ || !matchD || !matchT) continue;
            boolean solved = currentUser != null && currentUser.solved.contains(p.id);
            model.addRow(new Object[]{
                p.id, p.title,
                p.difficulty.name(), p.tag.name(),
                solved ? "✓ Solved" : "○ Unsolved"
            });
        }
    }

    static JTable buildProblemTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? BG_CARD : BG_PANEL);
                c.setForeground(TEXT_PRIMARY);
                if (isRowSelected(row)) c.setBackground(new Color(30, 60, 100));
                if (col == 2) {
                    String val = (String) model.getValueAt(row, col);
                    c.setForeground("EASY".equals(val) ? ACCENT2 :
                                    "MEDIUM".equals(val) ? ACCENT_WARN : ACCENT_RED);
                }
                if (col == 4) {
                    String val = (String) model.getValueAt(row, col);
                    c.setForeground(val.startsWith("✓") ? ACCENT2 : TEXT_MUTED);
                }
                return c;
            }
        };
        table.setFont(FONT_BODY);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(30, 60, 100));
        table.getTableHeader().setBackground(BG_PANEL);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        table.setColumnSelectionAllowed(false);

        // Column widths
        int[] widths = {40, 300, 90, 80, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        return table;
    }

    // ════════════════════════════════════════════════════════
    //  Problem Editor / Solve Screen
    // ════════════════════════════════════════════════════════
    static void openProblemEditor(Problem problem) {
        JDialog dialog = new JDialog(mainFrame, problem.title, true);
        dialog.setSize(1050, 700);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setBackground(BG_DARK);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(460);
        split.setBackground(BG_DARK);
        split.setBorder(null);
        split.setDividerSize(4);

        // ── LEFT: Problem Description ────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 16));

        Color diffColor = problem.difficulty == Difficulty.EASY ? ACCENT2 :
                          problem.difficulty == Difficulty.MEDIUM ? ACCENT_WARN : ACCENT_RED;
        JLabel diffBadge = label("  " + problem.difficulty.name() + "  ", FONT_BOLD, diffColor);
        diffBadge.setOpaque(true);
        diffBadge.setBackground(new Color(diffColor.getRed(), diffColor.getGreen(), diffColor.getBlue(), 30));
        diffBadge.setBorder(new LineBorder(diffColor, 1, true));

        JLabel titleLbl = label(problem.id + ". " + problem.title,
            new Font("SansSerif", Font.BOLD, 17), TEXT_PRIMARY);

        JTextArea descArea = new JTextArea(problem.description);
        descArea.setFont(FONT_BODY); descArea.setBackground(BG_PANEL);
        descArea.setForeground(TEXT_PRIMARY); descArea.setEditable(false);
        descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
        descArea.setBorder(null);

        JPanel exPanel = new JPanel();
        exPanel.setLayout(new BoxLayout(exPanel, BoxLayout.Y_AXIS));
        exPanel.setBackground(BG_CARD);
        exPanel.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 12, 10, 12)));

        exPanel.add(label("Example", new Font("SansSerif", Font.BOLD, 12), TEXT_MUTED));
        addGap(exPanel, 6);
        exPanel.add(monoLabel("Input:  " + problem.sampleInput));
        addGap(exPanel, 4);
        exPanel.add(monoLabel("Output: " + problem.sampleOutput));

        // Related problems via Graph BFS
        List<Integer> related = graph.related(problem.id, 3);
        JPanel relPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        relPanel.setOpaque(false);
        if (!related.isEmpty()) {
            relPanel.add(label("Related: ", FONT_BODY, TEXT_MUTED));
            for (int rid : related) {
                Problem rp = problems.get(rid);
                if (rp != null) {
                    JButton rb = ghostButton(rp.title);
                    rb.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    rb.addActionListener(e -> { dialog.dispose(); openProblemEditor(rp); });
                    relPanel.add(rb);
                }
            }
        }

        JPanel leftTop = new JPanel();
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.setOpaque(false);
        leftTop.add(titleLbl); addGap(leftTop, 8);
        leftTop.add(diffBadge); addGap(leftTop, 16);

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(null); styleScrollPane(descScroll);

        leftPanel.add(leftTop, BorderLayout.NORTH);
        leftPanel.add(descScroll, BorderLayout.CENTER);
        JPanel leftBottom = new JPanel(new BorderLayout());
        leftBottom.setOpaque(false);
        leftBottom.add(exPanel, BorderLayout.NORTH);
        addGap(leftBottom, 10);
        leftBottom.add(relPanel, BorderLayout.SOUTH);
        leftPanel.add(leftBottom, BorderLayout.SOUTH);

        // ── RIGHT: Code Editor ───────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_DARK);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(BG_PANEL);
        toolbar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(8, 12, 8, 12)));

        JLabel langLabel = label("Java", FONT_BOLD, ACCENT);

        // Timer display
        JLabel timerLabel = label("  ⏱  00:00", FONT_BOLD, ACCENT_WARN);
        int[] seconds = {0};
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            seconds[0]++;
            timerLabel.setText(String.format("  ⏱  %02d:%02d", seconds[0]/60, seconds[0]%60));
        });
        timer.start();
        dialog.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { timer.stop(); }
        });

        JButton hintBtn = ghostButton("💡 Hint");
        hintBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(dialog, problem.hint, "Hint 💡", JOptionPane.INFORMATION_MESSAGE));

        JPanel toolRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolRight.setOpaque(false);
        toolRight.add(timerLabel); toolRight.add(hintBtn);

        toolbar.add(langLabel, BorderLayout.WEST);
        toolbar.add(toolRight, BorderLayout.EAST);

        // Code editor
        JTextArea codeArea = new JTextArea();
        codeArea.setFont(FONT_MONO);
        codeArea.setBackground(new Color(10, 13, 18));
        codeArea.setForeground(new Color(200, 210, 230));
        codeArea.setCaretColor(ACCENT);
        codeArea.setLineWrap(false);
        codeArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        codeArea.setTabSize(4);
        codeArea.setText(getCodeTemplate(problem));

        JScrollPane codeScroll = new JScrollPane(codeArea);
        styleScrollPane(codeScroll);
        codeScroll.setRowHeaderView(buildLineNumbers(codeArea));

        // Bottom bar: Run + Submit
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(BG_PANEL);
        bottomBar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(10, 14, 10, 14)));

        JTextArea outputArea = new JTextArea(3, 40);
        outputArea.setFont(FONT_MONO);
        outputArea.setBackground(new Color(10, 13, 18));
        outputArea.setForeground(TEXT_PRIMARY);
        outputArea.setEditable(false);
        outputArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane outputScroll = new JScrollPane(outputArea);
        styleScrollPane(outputScroll);
        outputScroll.setPreferredSize(new Dimension(0, 80));

        JButton runBtn   = accentButton("▷ Run", new Color(40, 80, 140));
        JButton submitBtn = accentButton("Submit", ACCENT2);

        runBtn.addActionListener(e -> {
            timer.stop();
            outputArea.setForeground(ACCENT);
            outputArea.setText("Running against sample input...\n"
                + "Input:    " + problem.sampleInput + "\n"
                + "Expected: " + problem.sampleOutput + "\n"
                + "Your output:  " + problem.sampleOutput + "   ✓ Matched");
        });

        submitBtn.addActionListener(e -> {
            timer.stop();
            String code = codeArea.getText().trim();
            if (code.isEmpty() || code.equals(getCodeTemplate(problem))) {
                outputArea.setForeground(ACCENT_RED);
                outputArea.setText("✗ Empty submission. Write your solution first.");
                return;
            }
            // Simulate judge via Queue  ←  DSA: Queue
            Verdict verdict = simulateJudge(code);
            int pts = verdict == Verdict.ACCEPTED ?
                (problem.difficulty == Difficulty.EASY ? 100 :
                 problem.difficulty == Difficulty.MEDIUM ? 200 : 350) : 0;
            Submission sub = new Submission(currentUser.username, problem.id, code, verdict, pts);
            judgeQueue.add(sub);     // enqueue
            judgeQueue.poll();       // dequeue (processed)
            currentUser.history.add(sub);

            if (verdict == Verdict.ACCEPTED) {
                if (!currentUser.solved.contains(problem.id)) {
                    currentUser.solved.add(problem.id);
                    currentUser.solvedCount++;
                    currentUser.score += pts;
                    currentUser.streak++;
                }
                outputArea.setForeground(ACCENT2);
                outputArea.setText("✓  ACCEPTED  +  " + pts + " pts\n"
                    + "Runtime: " + (20 + (int)(Math.random()*80)) + " ms"
                    + "   Memory: " + (15 + (int)(Math.random()*10)) + " MB\n"
                    + "Your total score: " + currentUser.score);
            } else {
                outputArea.setForeground(ACCENT_RED);
                outputArea.setText("✗  " + verdict.name().replace('_', ' ') + "\n"
                    + "Review your logic and try again.");
            }
            refreshProblemList("", null, null);
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(runBtn); btnRow.add(submitBtn);

        bottomBar.add(outputScroll, BorderLayout.CENTER);
        bottomBar.add(btnRow, BorderLayout.EAST);

        rightPanel.add(toolbar, BorderLayout.NORTH);
        rightPanel.add(codeScroll, BorderLayout.CENTER);
        rightPanel.add(bottomBar, BorderLayout.SOUTH);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);

        dialog.setContentPane(split);
        dialog.setVisible(true);
    }

    // Simple line-number gutter
    static JTextArea buildLineNumbers(JTextArea codeArea) {
        JTextArea lines = new JTextArea("1");
        lines.setBackground(new Color(18, 22, 28));
        lines.setForeground(TEXT_MUTED);
        lines.setFont(FONT_MONO);
        lines.setEditable(false);
        lines.setBorder(new EmptyBorder(12, 6, 12, 6));
        codeArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                int count = codeArea.getLineCount();
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= count; i++) {
                    if (i > 1) sb.append('\n');
                    sb.append(i);
                }
                lines.setText(sb.toString());
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        return lines;
    }

    static Verdict simulateJudge(String code) {
        // Simple heuristic simulation
        if (code.length() < 30) return Verdict.COMPILE_ERROR;
        if (code.contains("TODO") || code.contains("// write")) return Verdict.WRONG_ANSWER;
        double r = Math.random();
        if (r < 0.72) return Verdict.ACCEPTED;
        if (r < 0.88) return Verdict.WRONG_ANSWER;
        return Verdict.TIME_LIMIT;
    }

    static String getCodeTemplate(Problem p) {
        return switch (p.tag) {
            case HASH -> """
                import java.util.*;
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        // Use a HashMap: num -> index
                        Map<Integer, Integer> map = new HashMap<>();
                        for (int i = 0; i < nums.length; i++) {
                            int complement = target - nums[i];
                            if (map.containsKey(complement))
                                return new int[]{ map.get(complement), i };
                            map.put(nums[i], i);
                        }
                        return new int[]{};
                    }
                }""";
            case STACK -> """
                import java.util.*;
                class Solution {
                    public boolean isValid(String s) {
                        // TODO: Use a Stack to match brackets
                        Stack<Character> stack = new Stack<>();
                        for (char c : s.toCharArray()) {
                            // write your logic here
                        }
                        return stack.isEmpty();
                    }
                }""";
            case GRAPH -> """
                import java.util.*;
                class Solution {
                    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
                    public int numIslands(char[][] grid) {
                        // TODO: DFS/BFS from each unvisited '1'
                        int count = 0;
                        for (int i = 0; i < grid.length; i++)
                            for (int j = 0; j < grid[0].length; j++)
                                if (grid[i][j] == '1') {
                                    dfs(grid, i, j);
                                    count++;
                                }
                        return count;
                    }
                    void dfs(char[][] g, int r, int c) {
                        // write your DFS here
                    }
                }""";
            case DP -> """
                class Solution {
                    public int maxSubArray(int[] nums) {
                        // TODO: Kadane's algorithm
                        int maxSum = nums[0], curSum = nums[0];
                        for (int i = 1; i < nums.length; i++) {
                            // write your DP logic here
                        }
                        return maxSum;
                    }
                }""";
            default -> """
                class Solution {
                    // Write your solution here
                    public void solve() {
                        // TODO
                    }
                }""";
        };
    }

    // ════════════════════════════════════════════════════════
    //  TAB: Leaderboard  (PriorityQueue / Max-Heap)
    // ════════════════════════════════════════════════════════
    static JPanel buildLeaderboardTab() {
        leaderboardPanel = darkPanel();
        leaderboardPanel.setLayout(new BorderLayout());
        leaderboardPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = label("🏆 Leaderboard", new Font("SansSerif", Font.BOLD, 20), TEXT_PRIMARY);
        JLabel sub   = label("Ranked by total score  ·  powered by Max-Heap (PriorityQueue)",
            FONT_BODY, TEXT_MUTED);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(title); addGap(header, 4); header.add(sub);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        leaderboardPanel.add(header, BorderLayout.NORTH);
        refreshLeaderboard();
        return leaderboardPanel;
    }

    static void refreshLeaderboard() {
        if (leaderboardPanel == null) return;
        // Remove old table if any
        if (leaderboardPanel.getComponentCount() > 1)
            leaderboardPanel.remove(leaderboardPanel.getComponent(1));

        List<User> top = leaderboard.getTop(20);
        String[] cols = {"Rank", "Username", "Score", "Solved", "Streak"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < top.size(); i++) {
            User u = top.get(i);
            String rank = i < 3 ? medals[i] : String.valueOf(i + 1);
            model.addRow(new Object[]{rank, u.username, u.score, u.solvedCount, u.streak + " 🔥"});
        }

        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? BG_CARD : BG_PANEL);
                c.setForeground(TEXT_PRIMARY);
                if (isRowSelected(row)) c.setBackground(new Color(30, 60, 100));
                // Highlight current user
                if (currentUser != null && model.getValueAt(row, 1).equals(currentUser.username))
                    c.setBackground(new Color(20, 50, 30));
                return c;
            }
        };
        table.setFont(FONT_BODY); table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY); table.setGridColor(BORDER);
        table.setRowHeight(38); table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(BG_PANEL);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER));

        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        leaderboardPanel.add(scroll, BorderLayout.CENTER);
        leaderboardPanel.revalidate();
        leaderboardPanel.repaint();
    }

    // ════════════════════════════════════════════════════════
    //  TAB: Profile
    // ════════════════════════════════════════════════════════
    static JPanel buildProfileTab() {
        JPanel outer = darkPanel();
        outer.setLayout(new BorderLayout());
        outer.setBorder(new EmptyBorder(20, 24, 20, 24));

        // We'll use a timer to refresh the profile view when shown
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setOpaque(false);

        // The profile content is rebuilt dynamically on show
        outer.putClientProperty("needsRefresh", true);
        outer.addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { buildProfileContent(outer); }
        });

        buildProfileContent(outer);
        return outer;
    }

    static void buildProfileContent(JPanel outer) {
        outer.removeAll();
        outer.setLayout(new BorderLayout(0, 16));

        if (currentUser == null) {
            outer.add(label("Not logged in.", FONT_BODY, TEXT_MUTED), BorderLayout.CENTER);
            outer.revalidate(); outer.repaint(); return;
        }

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);

        long easy   = currentUser.solved.stream().filter(id -> problems.containsKey(id) && problems.get(id).difficulty == Difficulty.EASY).count();
        long medium = currentUser.solved.stream().filter(id -> problems.containsKey(id) && problems.get(id).difficulty == Difficulty.MEDIUM).count();
        long hard   = currentUser.solved.stream().filter(id -> problems.containsKey(id) && problems.get(id).difficulty == Difficulty.HARD).count();

        statsRow.add(statCard("Total Score", String.valueOf(currentUser.score), ACCENT));
        statsRow.add(statCard("Solved", String.valueOf(currentUser.solvedCount), ACCENT2));
        statsRow.add(statCard("Streak", currentUser.streak + " days 🔥", ACCENT_WARN));
        statsRow.add(statCard("Easy / Med / Hard",
            easy + " / " + medium + " / " + hard, TEXT_PRIMARY));

        // Submission history
        JLabel histTitle = label("Submission History", new Font("SansSerif", Font.BOLD, 15), TEXT_PRIMARY);
        histTitle.setBorder(new EmptyBorder(4, 0, 8, 0));

        String[] cols = {"Problem", "Verdict", "Score", "Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Submission> hist = currentUser.history;
        for (int i = hist.size() - 1; i >= 0; i--) {
            Submission s = hist.get(i);
            Problem p = problems.get(s.problemId);
            String name = p != null ? p.title : "Problem " + s.problemId;
            String when = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(s.timestamp));
            model.addRow(new Object[]{name, s.verdict.name(), s.score > 0 ? "+" + s.score : "0", when});
        }

        JTable histTable = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? BG_CARD : BG_PANEL);
                c.setForeground(TEXT_PRIMARY);
                if (col == 1) {
                    String v = (String) model.getValueAt(row, col);
                    c.setForeground("ACCEPTED".equals(v) ? ACCENT2 : ACCENT_RED);
                }
                return c;
            }
        };
        histTable.setFont(FONT_BODY); histTable.setBackground(BG_CARD);
        histTable.setForeground(TEXT_PRIMARY); histTable.setGridColor(BORDER);
        histTable.setRowHeight(34); histTable.setShowVerticalLines(false);
        histTable.getTableHeader().setBackground(BG_PANEL);
        histTable.getTableHeader().setForeground(TEXT_MUTED);
        histTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(histTable);
        styleScrollPane(scroll);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(histTitle, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        JLabel profileTitle = label("👤  " + currentUser.username + "'s Profile",
            new Font("SansSerif", Font.BOLD, 20), TEXT_PRIMARY);
        profileTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        outer.add(profileTitle, BorderLayout.NORTH);
        outer.add(statsRow, BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(profileTitle, BorderLayout.NORTH);
        top.add(statsRow, BorderLayout.CENTER);

        outer.add(top, BorderLayout.NORTH);
        outer.add(center, BorderLayout.CENTER);
        outer.revalidate(); outer.repaint();
    }

    static JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)));

        JLabel t = label(title, new Font("SansSerif", Font.PLAIN, 11), TEXT_MUTED);
        JLabel v = label(value, new Font("SansSerif", Font.BOLD, 22), accent);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(t); addGap(card, 6); card.add(v);
        return card;
    }

    // ════════════════════════════════════════════════════════
    //  UI Helper Widgets
    // ════════════════════════════════════════════════════════
    static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_DARK);
        return p;
    }

    static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    static JLabel monoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_MONO);
        l.setForeground(new Color(140, 200, 255));
        return l;
    }

    static JLabel fieldLabel(String text) {
        JLabel l = label(text, new Font("SansSerif", Font.BOLD, 11), TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static JTextField styledField(String placeholder) {
        JTextField f = new JTextField(22) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    g.setColor(TEXT_MUTED);
                    g.setFont(getFont());
                    g.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        f.setBackground(BG_CARD); f.setForeground(TEXT_PRIMARY);
        f.setFont(FONT_BODY); f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    static JPasswordField styledPassField(String placeholder) {
        JPasswordField f = new JPasswordField(22) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    g.setColor(TEXT_MUTED);
                    g.setFont(getFont());
                    g.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        f.setBackground(BG_CARD); f.setForeground(TEXT_PRIMARY);
        f.setFont(FONT_BODY); f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    static JButton accentButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                             getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(FONT_BOLD); b.setPreferredSize(new Dimension(160, 36));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JButton ghostButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255,255,255,15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
                g2.setColor(getModel().isRollover() ? TEXT_PRIMARY : TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(FONT_BODY); b.setPreferredSize(new Dimension(180, 34));
        b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JButton navTabButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(new Color(255,255,255,10));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
                g2.setColor(getModel().isRollover() ? TEXT_PRIMARY : TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setFont(FONT_BOLD); b.setPreferredSize(new Dimension(110, 32));
        b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static void styleCombo(JComboBox<String> cb) {
        cb.setBackground(BG_CARD); cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(new LineBorder(BORDER, 1, true));
        cb.setPreferredSize(new Dimension(150, 34));
    }

    static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(new LineBorder(BORDER, 1));
        sp.getViewport().setBackground(BG_CARD);
        sp.getVerticalScrollBar().setBackground(BG_PANEL);
        sp.getHorizontalScrollBar().setBackground(BG_PANEL);
    }

    static void addGap(JPanel panel, int h) {
        panel.add(Box.createRigidArea(new Dimension(0, h)));
    }
}
