using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Deeco.Log
{
    class Program
    {
        public static void Main(string[] args)
        {
            string inputDir = "e:/tmp/logs/";
            string outputFile = "e:/tmp/results/ip-simple.csv";

            var files = Directory.EnumerateFiles(inputDir, "*.csv");
            string first = files.First();
            string firstLine = File.ReadLines(first).First();
            File.WriteAllText(outputFile, firstLine + "\n");

            long totalLines = 0;
            foreach (string file in files)
            {
                var lines = File.ReadAllLines(file).Skip(1);
                totalLines += lines.Count();
                File.AppendAllLines(outputFile, lines);
            }

            Console.WriteLine("Total lines: {0}", totalLines);
            Console.WriteLine("Done!");
            Console.ReadKey();
        }
    }
}
