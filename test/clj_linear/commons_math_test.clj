(ns clj-linear.commons-math-test
  (:use [clojure.test]
        [clj-linear.commons-math]))

(deftest to-double-array-test
  (is (= [0.0 1.0 2.0 3.0]
         (vec (to-double-array {1 1, 2 2, 3 3})))))

(deftest allocate-columns-test
  (is (= {:x 0, :y 1}
         (allocate-columns [{:x 3} {:y 9}]))))

(deftest coefficient-vector-test
  (is (= [4 2]
         (coefficient-vector {:x 4, :y 2} {:x 0, :y 1}))))

(deftest make-solution-map-test
  (is (= {:x 4, :y 2}
         (make-solution-map [4 2] {:x 0, :y 1}))))

#_(minimize (c/eqn  (+ (* -2 [:x 1]) :y -5))
          (c/eqns (<= (+ [:x 1] (* 2 :y)) 6)
                  (<= (+ (* 3 [:x 1]) (* 2 :y)) 12)
                  (>= :y 0)))


