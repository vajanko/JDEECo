﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class RequestSource
    {
        private string path;
        public RequestSource(string path)
        {
            this.path = path;
        }

        private IEnumerable<string> getLines()
        {
            List<string> files = new List<string>();
            foreach (string part in path.Split(';'))
            {
                if (File.Exists(part))
                    files.Add(part);
                else
                    files.AddRange(Directory.EnumerateFiles(part, "*.csv"));
            }

            string firstLine = File.ReadLines(files.First()).First();
            Request.InitHeader(firstLine);

            return files.SelectMany(f => File.ReadAllLines(f).Skip(1) );

            //if (File.Exists(path))
            //{
            //    string[] lines = File.ReadAllLines(path);
            //    Request.InitHeader(lines[0]);
            //    return lines.Skip(1);
            //}
            //else
            //{
            //    var files = Directory.EnumerateFiles(path, "*.csv");
            //    string first = files.First();
            //    string firstLine = File.ReadLines(first).First();
            //    Request.InitHeader(firstLine);

            //    return files.SelectMany(f => { try { return File.ReadAllLines(f).Skip(1); } catch { return Enumerable.Empty<string>(); } });
            //}
        }

        public IEnumerable<Request> GetRequests()
        {
            return getLines().Select(ln => new Request(ln));
        }
    }
}