using Matsim.Generator.Properties;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml.Linq;

namespace Matsim.Generator
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }

        private void generate()
        {
            string networkFilename;
            if (generateEdit.Checked)
                networkFilename = "network.xml";
            else if (loadEdit.Checked)
                networkFilename = Path.GetFileName(networkFileEdit.Text);
            else
                throw new NotImplementedException();

            ConfigGenerator conf = new ConfigGenerator(rootEdit.Text, configEdit.Text, outputEdit.Text,
                startEdit.Value.TimeOfDay, endEdit.Value.TimeOfDay, (int)simStepEdit.Value, networkFilename);
            conf.Generate();

            Graph graph;

            if (generateEdit.Checked)
            {
                NetworkGenerator net = new NetworkGenerator(conf);
                net.Width = (int)widthEdit.Value;
                net.Height = (int)heightEdit.Value;
                net.Step = (double)stepEdit.Value;
                net.Left = (double)leftEdit.Value;
                net.Bottom = (double)bottomEdit.Value;
                net.Generate();

                graph = Graph.LoadNetwork(net.Root);
            }
            else if (loadEdit.Checked)
            {
                CloneNetworkGenerator net = new CloneNetworkGenerator(conf, networkFileEdit.Text);
                net.Generate();

                graph = Graph.LoadNetwork(net.Root);
            }
            else
            {
                throw new NotImplementedException();
            }

            VehicleGenerator veh = new VehicleGenerator(conf, (int)vehCountEdit.Value);
            veh.Generate();

            ScheduleGenerator sch = new ScheduleGenerator(conf, graph, veh, (int)transitsEdit.Value, (int)groupSizeEdit.Value);
            sch.Generate();

            PopulationGenerator pop = new PopulationGenerator(conf, graph, (int)popCountEdit.Value, (int)activitiesEdit.Value);
            pop.Generate();
        }
        private void generateButton_Click(object sender, EventArgs e)
        {
            Button button = sender as Button;
            
            string previousText = button.Text;
            button.Text = "Working ...";
            button.Enabled = false;

            generate();

            button.Enabled = true;
            button.Text = previousText;
        }

        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            Settings.Default.Save();
        }

        private void browseNetworkButton_Click(object sender, EventArgs e)
        {
            if (openFileDialog.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                networkFileEdit.Text = openFileDialog.FileName;
            }
        }

        private void browseRootButton_Click(object sender, EventArgs e)
        {
            if (openFolderDialog.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                rootEdit.Text = openFolderDialog.SelectedPath;
            }
        }
    }
}
