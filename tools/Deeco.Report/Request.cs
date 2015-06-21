using System
;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class Request
    {
        private static string[] header;
        public static void InitHeader(string data)
        {
            header = data.Split(';');
        }

        private string []parts;

        public Request(string data)
        {
            this.parts = data.Split(';');
        }


        public string GetString(string name)
        {
            int idx = Array.IndexOf(header, name);
            return parts[idx];
        }
        public int GetInt(string name)
        {
            return int.Parse(GetString(name));
        }
        public double GetDouble(string name)
        {
            return double.Parse(GetString(name), System.Globalization.CultureInfo.InvariantCulture);
        }
    }
}
