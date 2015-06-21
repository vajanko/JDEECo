namespace Matsim.Generator
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.generateButton = new System.Windows.Forms.Button();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.vehCountEdit = new System.Windows.Forms.NumericUpDown();
            this.label6 = new System.Windows.Forms.Label();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.browseRootButton = new System.Windows.Forms.Button();
            this.label12 = new System.Windows.Forms.Label();
            this.outputEdit = new System.Windows.Forms.TextBox();
            this.configEdit = new System.Windows.Forms.TextBox();
            this.label11 = new System.Windows.Forms.Label();
            this.label10 = new System.Windows.Forms.Label();
            this.rootEdit = new System.Windows.Forms.TextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.endEdit = new System.Windows.Forms.DateTimePicker();
            this.label7 = new System.Windows.Forms.Label();
            this.startEdit = new System.Windows.Forms.DateTimePicker();
            this.groupBox4 = new System.Windows.Forms.GroupBox();
            this.label14 = new System.Windows.Forms.Label();
            this.transitsEdit = new System.Windows.Forms.NumericUpDown();
            this.label9 = new System.Windows.Forms.Label();
            this.groupBox5 = new System.Windows.Forms.GroupBox();
            this.activitiesEdit = new System.Windows.Forms.NumericUpDown();
            this.label13 = new System.Windows.Forms.Label();
            this.popCountEdit = new System.Windows.Forms.NumericUpDown();
            this.groupBox6 = new System.Windows.Forms.GroupBox();
            this.bottomEdit = new System.Windows.Forms.NumericUpDown();
            this.generateEdit = new System.Windows.Forms.RadioButton();
            this.stepEdit = new System.Windows.Forms.NumericUpDown();
            this.leftEdit = new System.Windows.Forms.NumericUpDown();
            this.loadEdit = new System.Windows.Forms.RadioButton();
            this.browseNetworkButton = new System.Windows.Forms.Button();
            this.networkFileEdit = new System.Windows.Forms.TextBox();
            this.widthEdit = new System.Windows.Forms.NumericUpDown();
            this.heightEdit = new System.Windows.Forms.NumericUpDown();
            this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
            this.openFolderDialog = new System.Windows.Forms.FolderBrowserDialog();
            this.groupBox2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.vehCountEdit)).BeginInit();
            this.groupBox3.SuspendLayout();
            this.groupBox4.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.transitsEdit)).BeginInit();
            this.groupBox5.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.activitiesEdit)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.popCountEdit)).BeginInit();
            this.groupBox6.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.bottomEdit)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.stepEdit)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.leftEdit)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.widthEdit)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.heightEdit)).BeginInit();
            this.SuspendLayout();
            // 
            // generateButton
            // 
            this.generateButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.generateButton.Location = new System.Drawing.Point(481, 304);
            this.generateButton.Name = "generateButton";
            this.generateButton.Size = new System.Drawing.Size(75, 23);
            this.generateButton.TabIndex = 0;
            this.generateButton.Text = "Generate";
            this.generateButton.UseVisualStyleBackColor = true;
            this.generateButton.Click += new System.EventHandler(this.generateButton_Click);
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(374, 59);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(32, 13);
            this.label5.TabIndex = 9;
            this.label5.Text = "Step:";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(243, 84);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(43, 13);
            this.label4.TabIndex = 6;
            this.label4.Text = "Bottom:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(243, 59);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(28, 13);
            this.label3.TabIndex = 5;
            this.label3.Text = "Left:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(110, 83);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(41, 13);
            this.label2.TabIndex = 1;
            this.label2.Text = "Height:";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(110, 59);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(38, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Width:";
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.vehCountEdit);
            this.groupBox2.Controls.Add(this.label6);
            this.groupBox2.Location = new System.Drawing.Point(12, 232);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(149, 94);
            this.groupBox2.TabIndex = 2;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Vehicles";
            // 
            // vehCountEdit
            // 
            this.vehCountEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "VehicleCount", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.vehCountEdit.Location = new System.Drawing.Point(58, 19);
            this.vehCountEdit.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.vehCountEdit.Name = "vehCountEdit";
            this.vehCountEdit.Size = new System.Drawing.Size(74, 20);
            this.vehCountEdit.TabIndex = 4;
            this.vehCountEdit.Value = global::Matsim.Generator.Properties.Settings.Default.VehicleCount;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(14, 21);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(38, 13);
            this.label6.TabIndex = 3;
            this.label6.Text = "Count:";
            // 
            // groupBox3
            // 
            this.groupBox3.Controls.Add(this.browseRootButton);
            this.groupBox3.Controls.Add(this.label12);
            this.groupBox3.Controls.Add(this.outputEdit);
            this.groupBox3.Controls.Add(this.configEdit);
            this.groupBox3.Controls.Add(this.label11);
            this.groupBox3.Controls.Add(this.label10);
            this.groupBox3.Controls.Add(this.rootEdit);
            this.groupBox3.Controls.Add(this.label8);
            this.groupBox3.Controls.Add(this.endEdit);
            this.groupBox3.Controls.Add(this.label7);
            this.groupBox3.Controls.Add(this.startEdit);
            this.groupBox3.Location = new System.Drawing.Point(12, 12);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(546, 97);
            this.groupBox3.TabIndex = 3;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "Config";
            // 
            // browseRootButton
            // 
            this.browseRootButton.Location = new System.Drawing.Point(465, 16);
            this.browseRootButton.Name = "browseRootButton";
            this.browseRootButton.Size = new System.Drawing.Size(75, 23);
            this.browseRootButton.TabIndex = 10;
            this.browseRootButton.Text = "Browse ...";
            this.browseRootButton.UseVisualStyleBackColor = true;
            this.browseRootButton.Click += new System.EventHandler(this.browseRootButton_Click);
            // 
            // label12
            // 
            this.label12.AutoSize = true;
            this.label12.Location = new System.Drawing.Point(134, 73);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(42, 13);
            this.label12.TabIndex = 9;
            this.label12.Text = "Output:";
            // 
            // outputEdit
            // 
            this.outputEdit.DataBindings.Add(new System.Windows.Forms.Binding("Text", global::Matsim.Generator.Properties.Settings.Default, "Output", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.outputEdit.Location = new System.Drawing.Point(182, 70);
            this.outputEdit.Name = "outputEdit";
            this.outputEdit.Size = new System.Drawing.Size(277, 20);
            this.outputEdit.TabIndex = 8;
            this.outputEdit.Text = global::Matsim.Generator.Properties.Settings.Default.Output;
            // 
            // configEdit
            // 
            this.configEdit.DataBindings.Add(new System.Windows.Forms.Binding("Text", global::Matsim.Generator.Properties.Settings.Default, "Config", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.configEdit.Location = new System.Drawing.Point(182, 44);
            this.configEdit.Name = "configEdit";
            this.configEdit.Size = new System.Drawing.Size(277, 20);
            this.configEdit.TabIndex = 7;
            this.configEdit.Text = global::Matsim.Generator.Properties.Settings.Default.Config;
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(136, 47);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(40, 13);
            this.label11.TabIndex = 6;
            this.label11.Text = "Config:";
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(143, 21);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(33, 13);
            this.label10.TabIndex = 5;
            this.label10.Text = "Root:";
            // 
            // rootEdit
            // 
            this.rootEdit.DataBindings.Add(new System.Windows.Forms.Binding("Text", global::Matsim.Generator.Properties.Settings.Default, "Root", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.rootEdit.Location = new System.Drawing.Point(182, 18);
            this.rootEdit.Name = "rootEdit";
            this.rootEdit.Size = new System.Drawing.Size(277, 20);
            this.rootEdit.TabIndex = 4;
            this.rootEdit.Text = global::Matsim.Generator.Properties.Settings.Default.Root;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(9, 47);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(29, 13);
            this.label8.TabIndex = 3;
            this.label8.Text = "End:";
            // 
            // endEdit
            // 
            this.endEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "End", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.endEdit.Format = System.Windows.Forms.DateTimePickerFormat.Time;
            this.endEdit.Location = new System.Drawing.Point(44, 44);
            this.endEdit.Name = "endEdit";
            this.endEdit.ShowUpDown = true;
            this.endEdit.Size = new System.Drawing.Size(85, 20);
            this.endEdit.TabIndex = 2;
            this.endEdit.Value = global::Matsim.Generator.Properties.Settings.Default.End;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(6, 21);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(32, 13);
            this.label7.TabIndex = 1;
            this.label7.Text = "Start:";
            // 
            // startEdit
            // 
            this.startEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "Start", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.startEdit.Format = System.Windows.Forms.DateTimePickerFormat.Time;
            this.startEdit.Location = new System.Drawing.Point(44, 18);
            this.startEdit.Name = "startEdit";
            this.startEdit.ShowUpDown = true;
            this.startEdit.Size = new System.Drawing.Size(85, 20);
            this.startEdit.TabIndex = 0;
            this.startEdit.Value = global::Matsim.Generator.Properties.Settings.Default.Start;
            // 
            // groupBox4
            // 
            this.groupBox4.Controls.Add(this.label14);
            this.groupBox4.Controls.Add(this.transitsEdit);
            this.groupBox4.Location = new System.Drawing.Point(167, 232);
            this.groupBox4.Name = "groupBox4";
            this.groupBox4.Size = new System.Drawing.Size(149, 94);
            this.groupBox4.TabIndex = 4;
            this.groupBox4.TabStop = false;
            this.groupBox4.Text = "Schedule";
            // 
            // label14
            // 
            this.label14.AutoSize = true;
            this.label14.Location = new System.Drawing.Point(6, 21);
            this.label14.Name = "label14";
            this.label14.Size = new System.Drawing.Size(47, 13);
            this.label14.TabIndex = 6;
            this.label14.Text = "Transits:";
            // 
            // transitsEdit
            // 
            this.transitsEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "TransitCount", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.transitsEdit.Location = new System.Drawing.Point(69, 19);
            this.transitsEdit.Name = "transitsEdit";
            this.transitsEdit.Size = new System.Drawing.Size(74, 20);
            this.transitsEdit.TabIndex = 0;
            this.transitsEdit.Value = global::Matsim.Generator.Properties.Settings.Default.TransitCount;
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(23, 21);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(38, 13);
            this.label9.TabIndex = 5;
            this.label9.Text = "Count:";
            // 
            // groupBox5
            // 
            this.groupBox5.Controls.Add(this.activitiesEdit);
            this.groupBox5.Controls.Add(this.label13);
            this.groupBox5.Controls.Add(this.popCountEdit);
            this.groupBox5.Controls.Add(this.label9);
            this.groupBox5.Location = new System.Drawing.Point(322, 232);
            this.groupBox5.Name = "groupBox5";
            this.groupBox5.Size = new System.Drawing.Size(149, 94);
            this.groupBox5.TabIndex = 5;
            this.groupBox5.TabStop = false;
            this.groupBox5.Text = "Population";
            // 
            // activitiesEdit
            // 
            this.activitiesEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "ActivitiesCount", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.activitiesEdit.Location = new System.Drawing.Point(67, 45);
            this.activitiesEdit.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.activitiesEdit.Name = "activitiesEdit";
            this.activitiesEdit.Size = new System.Drawing.Size(74, 20);
            this.activitiesEdit.TabIndex = 8;
            this.activitiesEdit.Value = global::Matsim.Generator.Properties.Settings.Default.ActivitiesCount;
            // 
            // label13
            // 
            this.label13.AutoSize = true;
            this.label13.Location = new System.Drawing.Point(9, 47);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(52, 13);
            this.label13.TabIndex = 7;
            this.label13.Text = "Activities:";
            // 
            // popCountEdit
            // 
            this.popCountEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "PopulationCount", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.popCountEdit.Location = new System.Drawing.Point(67, 19);
            this.popCountEdit.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.popCountEdit.Name = "popCountEdit";
            this.popCountEdit.Size = new System.Drawing.Size(74, 20);
            this.popCountEdit.TabIndex = 6;
            this.popCountEdit.Value = global::Matsim.Generator.Properties.Settings.Default.PopulationCount;
            // 
            // groupBox6
            // 
            this.groupBox6.Controls.Add(this.bottomEdit);
            this.groupBox6.Controls.Add(this.label5);
            this.groupBox6.Controls.Add(this.label4);
            this.groupBox6.Controls.Add(this.generateEdit);
            this.groupBox6.Controls.Add(this.label3);
            this.groupBox6.Controls.Add(this.stepEdit);
            this.groupBox6.Controls.Add(this.leftEdit);
            this.groupBox6.Controls.Add(this.loadEdit);
            this.groupBox6.Controls.Add(this.browseNetworkButton);
            this.groupBox6.Controls.Add(this.networkFileEdit);
            this.groupBox6.Controls.Add(this.widthEdit);
            this.groupBox6.Controls.Add(this.heightEdit);
            this.groupBox6.Controls.Add(this.label1);
            this.groupBox6.Controls.Add(this.label2);
            this.groupBox6.Location = new System.Drawing.Point(12, 115);
            this.groupBox6.Name = "groupBox6";
            this.groupBox6.Size = new System.Drawing.Size(546, 111);
            this.groupBox6.TabIndex = 6;
            this.groupBox6.TabStop = false;
            this.groupBox6.Text = "Network";
            // 
            // bottomEdit
            // 
            this.bottomEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "Bottom", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.bottomEdit.DecimalPlaces = 2;
            this.bottomEdit.Location = new System.Drawing.Point(292, 81);
            this.bottomEdit.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.bottomEdit.Name = "bottomEdit";
            this.bottomEdit.Size = new System.Drawing.Size(74, 20);
            this.bottomEdit.TabIndex = 7;
            this.bottomEdit.ThousandsSeparator = true;
            this.bottomEdit.Value = global::Matsim.Generator.Properties.Settings.Default.Bottom;
            // 
            // generateEdit
            // 
            this.generateEdit.AutoSize = true;
            this.generateEdit.Location = new System.Drawing.Point(22, 55);
            this.generateEdit.Name = "generateEdit";
            this.generateEdit.Size = new System.Drawing.Size(69, 17);
            this.generateEdit.TabIndex = 16;
            this.generateEdit.Text = "Generate";
            this.generateEdit.UseVisualStyleBackColor = true;
            // 
            // stepEdit
            // 
            this.stepEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "Step", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.stepEdit.DecimalPlaces = 2;
            this.stepEdit.Location = new System.Drawing.Point(412, 57);
            this.stepEdit.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.stepEdit.Name = "stepEdit";
            this.stepEdit.Size = new System.Drawing.Size(74, 20);
            this.stepEdit.TabIndex = 8;
            this.stepEdit.ThousandsSeparator = true;
            this.stepEdit.Value = global::Matsim.Generator.Properties.Settings.Default.Step;
            // 
            // leftEdit
            // 
            this.leftEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "Left", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.leftEdit.DecimalPlaces = 2;
            this.leftEdit.Location = new System.Drawing.Point(292, 55);
            this.leftEdit.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.leftEdit.Name = "leftEdit";
            this.leftEdit.Size = new System.Drawing.Size(74, 20);
            this.leftEdit.TabIndex = 4;
            this.leftEdit.ThousandsSeparator = true;
            this.leftEdit.Value = global::Matsim.Generator.Properties.Settings.Default.Left;
            // 
            // loadEdit
            // 
            this.loadEdit.AutoSize = true;
            this.loadEdit.Checked = true;
            this.loadEdit.Location = new System.Drawing.Point(22, 23);
            this.loadEdit.Name = "loadEdit";
            this.loadEdit.Size = new System.Drawing.Size(49, 17);
            this.loadEdit.TabIndex = 15;
            this.loadEdit.TabStop = true;
            this.loadEdit.Text = "Load";
            this.loadEdit.UseVisualStyleBackColor = true;
            // 
            // browseNetworkButton
            // 
            this.browseNetworkButton.Location = new System.Drawing.Point(465, 17);
            this.browseNetworkButton.Name = "browseNetworkButton";
            this.browseNetworkButton.Size = new System.Drawing.Size(75, 23);
            this.browseNetworkButton.TabIndex = 13;
            this.browseNetworkButton.Text = "Browse ...";
            this.browseNetworkButton.UseVisualStyleBackColor = true;
            this.browseNetworkButton.Click += new System.EventHandler(this.browseNetworkButton_Click);
            // 
            // networkFileEdit
            // 
            this.networkFileEdit.DataBindings.Add(new System.Windows.Forms.Binding("Text", global::Matsim.Generator.Properties.Settings.Default, "NetworkFile", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.networkFileEdit.Location = new System.Drawing.Point(113, 19);
            this.networkFileEdit.Name = "networkFileEdit";
            this.networkFileEdit.Size = new System.Drawing.Size(346, 20);
            this.networkFileEdit.TabIndex = 11;
            this.networkFileEdit.Text = global::Matsim.Generator.Properties.Settings.Default.NetworkFile;
            // 
            // widthEdit
            // 
            this.widthEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "Width", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.widthEdit.Location = new System.Drawing.Point(163, 55);
            this.widthEdit.Name = "widthEdit";
            this.widthEdit.Size = new System.Drawing.Size(74, 20);
            this.widthEdit.TabIndex = 2;
            this.widthEdit.Value = global::Matsim.Generator.Properties.Settings.Default.Width;
            // 
            // heightEdit
            // 
            this.heightEdit.DataBindings.Add(new System.Windows.Forms.Binding("Value", global::Matsim.Generator.Properties.Settings.Default, "Height", true, System.Windows.Forms.DataSourceUpdateMode.OnPropertyChanged));
            this.heightEdit.Location = new System.Drawing.Point(163, 81);
            this.heightEdit.Name = "heightEdit";
            this.heightEdit.Size = new System.Drawing.Size(74, 20);
            this.heightEdit.TabIndex = 3;
            this.heightEdit.Value = global::Matsim.Generator.Properties.Settings.Default.Height;
            // 
            // openFileDialog
            // 
            this.openFileDialog.FileName = "network.xml";
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(568, 339);
            this.Controls.Add(this.groupBox6);
            this.Controls.Add(this.groupBox5);
            this.Controls.Add(this.groupBox4);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.generateButton);
            this.Name = "MainForm";
            this.Text = "Matsim Transit Generator";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.vehCountEdit)).EndInit();
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.groupBox4.ResumeLayout(false);
            this.groupBox4.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.transitsEdit)).EndInit();
            this.groupBox5.ResumeLayout(false);
            this.groupBox5.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.activitiesEdit)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.popCountEdit)).EndInit();
            this.groupBox6.ResumeLayout(false);
            this.groupBox6.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.bottomEdit)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.stepEdit)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.leftEdit)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.widthEdit)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.heightEdit)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button generateButton;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.NumericUpDown heightEdit;
        private System.Windows.Forms.NumericUpDown widthEdit;
        private System.Windows.Forms.NumericUpDown leftEdit;
        private System.Windows.Forms.NumericUpDown bottomEdit;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.NumericUpDown stepEdit;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.NumericUpDown vehCountEdit;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.GroupBox groupBox3;
        private System.Windows.Forms.GroupBox groupBox4;
        private System.Windows.Forms.GroupBox groupBox5;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.DateTimePicker endEdit;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.DateTimePicker startEdit;
        private System.Windows.Forms.NumericUpDown popCountEdit;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Label label12;
        private System.Windows.Forms.TextBox outputEdit;
        private System.Windows.Forms.TextBox configEdit;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.TextBox rootEdit;
        private System.Windows.Forms.Button browseRootButton;
        private System.Windows.Forms.NumericUpDown activitiesEdit;
        private System.Windows.Forms.Label label13;
        private System.Windows.Forms.GroupBox groupBox6;
        private System.Windows.Forms.Button browseNetworkButton;
        private System.Windows.Forms.TextBox networkFileEdit;
        private System.Windows.Forms.RadioButton generateEdit;
        private System.Windows.Forms.RadioButton loadEdit;
        private System.Windows.Forms.OpenFileDialog openFileDialog;
        private System.Windows.Forms.Label label14;
        private System.Windows.Forms.NumericUpDown transitsEdit;
        private System.Windows.Forms.FolderBrowserDialog openFolderDialog;
    }
}

