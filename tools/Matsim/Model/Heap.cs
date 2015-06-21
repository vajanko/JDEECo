using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Matsim.Generator
{
    public class Heap<T> where T: IComparable
    {
        public int Degree { get; private set; }
        private Func<IComparable, IComparable, bool> _compare;
        public int Count { get; private set; }

        public T Root { get { return data[0]; } }

        private T[] data;

        private bool compare(int a, int b)
        {
            return _compare(data[a], data[b]);
        }
        private void build()
        {
            this.Count = data.Length;
            int startIndex = this.Count / this.Degree - 1;

            for (int i = startIndex; i >= 0; i--)
            {
                heapifyAll(i);
            }
        }
        private int heapify(int currentIndex)
        {
            // index of the most left child - check whether there the left child even exists
            int leftChild = getLeftChildIndex(currentIndex);
            if (leftChild >= this.Count)
                return currentIndex;

            // index of the most right child - check whether there are some childs at the end
            int rightChild = getRightChildIndex(currentIndex);
            rightChild = rightChild < this.Count ? rightChild : this.Count - 1;

            // index of the child item that should be placed most upper from all childs of the current item
            int upperIndex = leftChild;

            for (int childIndex = leftChild + 1; childIndex <= rightChild; leftChild++)
            {
                upperIndex = getUpperIndex(upperIndex, childIndex);
            }

            if (compare(currentIndex, upperIndex))
            {
                swap(currentIndex, upperIndex);
                return upperIndex;
            }

            // nothing has changed, the current item remains in the same place
            return currentIndex;
        }
        private void heapifyAll(int currentIndex)
        {
            int lowerIndex = currentIndex;
            do
            {
                currentIndex = lowerIndex;
                lowerIndex = heapify(currentIndex);

            } while (currentIndex != lowerIndex);
        }

        public T ExtractRoot()
        {
            T root = Root;
            T last = data[Count - 1];
            Count -= 1;

            data[0] = last;
            heapifyAll(0);

            return root;
        }

        public Heap(int degree = 2)
        {
            if (degree < 2)
                throw new ArgumentOutOfRangeException("degree", "Heap degree must be greater than two.");

            this.Degree = degree;
            this._compare = minStrategy;
        }
        private Heap(T[] array, int degree = 2)
            : this(degree)
        {
            this.data = array;
        }

        private bool minStrategy(IComparable a, IComparable b)
        {
            return a.CompareTo(b) > 0;
        }
        private bool maxStrategy(IComparable a, IComparable b)
        {
            return a.CompareTo(b) < 0;
        }
        private void swap(int a, int b)
        {
            T tmp = data[a];
            data[a] = data[b];
            data[b] = tmp;
        }

        private int getUpperIndex(int a, int b)
        {
            return compare(a, b) ? b : a;
        }
        private int getParentIndex(int currentIndex)
        {
            return (currentIndex - 1) / Degree;
        }
        private int getLeftChildIndex(int currentIndex)
        {
            return currentIndex * Degree + 1;
            //return currentIndex * Degree + 1;
        }
        private int getRightChildIndex(int currentIndex)
        {
            return (currentIndex + 1) * Degree;
            //return currentIndex * Degree + 2;
        }

        public static Heap<T> BuildFrom(T[] array)
        {
            Heap<T> heap = new Heap<T>(array);

            heap.build();

            return heap;
        }
    }
}
