using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using Matsim;

namespace Matsim.Generator
{
    class VehicleGenerator : GeneratorBase
    {
        protected IdProvider vehicleId = new IdProvider();

        public int Count { get; set; }

        public IEnumerable<XElement> GetVehicles()
        {
            return Root.Elements(Root.GetDefaultNamespace() + "vehicle");
        }

        public VehicleGenerator(ConfigGenerator conf, int count)
            : base(conf, "Templates/vehicles.xml", "vehicles.xml", "Templates/vehicleDefinitions_v1.0.xsd")
        {
            this.Count = count;
        }

        protected override void generateInternal(XDocument doc)
        {
            XElement vehicles = doc.Root;

            for (int i = 0; i < Count; ++i)
            {
                vehicles.Add(createVehicle(1));
            }
        }

        protected XElement createVehicle(int type)
        {
            string id = vehicleId.CreateId();

            XElement vehicle = new XElement(Root.GetDefaultNamespace() + "vehicle",
                new XAttribute("id", id),
                new XAttribute("type", type));

            return vehicle;
        }
    }
}
