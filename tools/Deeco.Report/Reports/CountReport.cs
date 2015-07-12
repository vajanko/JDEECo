using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class CountReport : IReport
    {
        private string path;
        public CountReport(string inputPath)
        {
            this.path = inputPath;
        }
        private IEnumerable<string> getFiles()
        {
            List<string> files = new List<string>();
            foreach (string part in path.Split(';'))
            {
                if (File.Exists(part))
                    files.Add(part);
                else
                    files.AddRange(Directory.EnumerateFiles(part, "*.csv"));
            }

            return files;
        }



        public void GenerateReport(string outputFile)
        {
            File.Delete(outputFile);
            using (var writer = new StreamWriter(File.OpenWrite(outputFile)))
            {
                writer.WriteLine("HDPeriod;PLPeriod;Probability;LocalTimeout;GlobalTimeout;Type;Count");

                //Parallel.ForEach(getFiles(), file =>
                foreach (var file in getFiles())
                {
                    Console.WriteLine(file);
                    var lines = File.ReadAllLines(file);
                    Request.InitHeader(lines.First());

                    var requests = lines.Skip(1)
                        .Where(ln => ln.Contains("SEND"))
                        .Select(ln => new Request(ln));
                        //.Where(r => r.GetString("Action") == "SEND");

                    writeFile(writer, requests);
                }
                //});
            }
        }
        private void writeFile(StreamWriter writer, IEnumerable<Request> requests)
        {
            Request first = requests.First();
            var hd = first.GetInt("HDPeriod");
            var pl = first.GetInt("PLPeriod");
            var prob = first.GetDouble("Probability");
            var loc = first.GetInt("LocalTimeout");
            var glob = first.GetInt("GlobalTimeout");

            var values = requests.Select(r => r.GetString("Type")).GroupBy(i => i)
                .Select(g => new
                {
                    Type = g.Key,
                    Count = g.Count()
                }).ToArray();

            lock (writer)
            {
                foreach (var i in values)
                {
                    writer.Write("{0};{1};{2};{3};{4};", hd, pl, prob, loc, glob);
                    writer.WriteLine("{0};{1}", i.Type, i.Count);
                }
            }
        }
    }
}
