import java.util.HashMap;

public class Trie {
    // The root node of the trie
    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode node = current.children.get(c);
            if (node == null) {
                node = new TrieNode();
                current.children.put(c, node);
            }
            current = node;
        }
        current.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode node = current.children.get(c);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEnd;
    }

    public class TrieNode {
        // Indicates whether this node is the end of a word
        public boolean isEnd;

        // The children of this node
        public HashMap<Character, TrieNode> children;

        public TrieNode() {
            isEnd = false;
            children = new HashMap<>();
        }
    }
}
