(ns clj-linear.expression-test
  (:refer-clojure :exclude [+ - *])
  (:use clj-linear.expression
        clojure.test))

(deftest coefficient-test
  (is (= 1 (coefficient {:a 1, :b 2} :a)))
  (is (= 0 (coefficient {:a 1, :b 2} :c))))

(deftest constant-test
  (is (= 3 (constant {:a 1, :b 2, nil 3})))
  (is (= 0 (constant {:a 1, :b 2}))))

(deftest add-test
  (is (= {:a 1, :b 2} (+ {:a 1} {:b 2})))
  (is (= {:a 1, :b 2} (+ {:a 1, :b 1} {:b 1}))))

(deftest subtract-test
  (is (= {:a 1, :b 1} (- {:a 1, :b 2} {:b 1})))
  (is (= {:a 1, :b -1} (- {:a 1} {:b 1}))))

(deftest multiply-test
  (is (= {:a 2, :b 4} (* {:a 1, :b 2} 2))))

(deftest remove-zero-terms-test
  (is (= {:a 1, :b 2} (remove-zero-terms {:a 1, :b 2, :c 0}))))

(deftest substitute-test
  (is (= {:b 2} (substitute {:a 1, :b 1} :a {:b 1})))
  (is (= {:b 5} (substitute {:a 2, :b 1} :a {:b 2}))))

