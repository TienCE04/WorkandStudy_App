package com.example.workandstudy_app.document.Document_Activity

import com.example.workandstudy_app.document.Entity.Subjects

class SearchDoc {
    // Node của Trie
    class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        var isEnd = false
        var subject: Subjects? = null // Lưu đối tượng Subjects tại node kết thúc
    }

    // Trie để lưu danh sách Subjects
    class Trie {
        private val root = TrieNode()

        // Thêm một môn học vào Trie
        fun insert(subject: Subjects) {
            var node = root
            val word = subject.tenMonHoc.lowercase() // Không phân biệt hoa thường
            for (char in word) {
                node = node.children.getOrPut(char) { TrieNode() }
            }
            node.isEnd = true
            node.subject = subject // Lưu Subjects tại node cuối
        }

        // Tìm kiếm các môn học có tiền tố
        fun searchPrefix(prefix: String): List<Subjects> {
            var node = root
            val lowerPrefix = prefix.lowercase()
            for (char in lowerPrefix) {
                node = node.children[char] ?: return emptyList()
            }
            return collectSubjects(node)
        }

        // Thu thập tất cả Subjects từ một node
        private fun collectSubjects(node: TrieNode): List<Subjects> {
            val result = mutableListOf<Subjects>()
            if (node.isEnd && node.subject != null) {
                result.add(node.subject!!)
            }
            for ((_, child) in node.children) {
                result.addAll(collectSubjects(child))
            }
            return result
        }
    }
}