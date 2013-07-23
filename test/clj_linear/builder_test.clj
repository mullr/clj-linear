(ns clj-linear.builder
  (:require [clj-linear.expression :as expr])
  (:use [clojure.test]
        [clj-linear.builder]))

(deftest term-test
  (is (= {nil 1} (term 1)))
  (is (= {:x  1} (term :x)))
  (is (= {:x 42} (term {:x 42})))
  (is (= {:x 42} (term :x 42)))
  (is (= {:x 42} (term 42 :x))))

(deftest expression-test
  (is (= {:x  42} (expression {:x 42})))
  (is (= {:x   1} (expression :x)))
  (is (= {nil 42} (expression 42))))

(deftest build-test
  (is (= {:x 1, :y 2} (plus :x (times :y 2))))
  (is (= {:op :=, :lhs {:x 1, :y -1}, :rhs {nil 42}}
         (equals (plus :x (minus :y) -42) 0))))
