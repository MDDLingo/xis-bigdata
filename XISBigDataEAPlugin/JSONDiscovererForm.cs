using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using System.Runtime.Remoting.Messaging;
using System.Reflection;
using System.Threading;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace XISBigDataEAPlugin
{
    public partial class JSONDiscovererForm : Form
    {
        private const bool GENERATE = true;
        private const string noFile = "Select a file...";
        private const string noURL = "Paste a URL...";
        private EA.Repository repository;
        private string jsonText;

        public JSONDiscovererForm(EA.Repository repository)
        {
            InitializeComponent();
            this.repository = repository;
        }

        private void buttonBrowse_Click(object sender, EventArgs e)
        {
            openFileDialog1.ShowDialog();

            if (!string.IsNullOrEmpty(openFileDialog1.FileName))
            {
                textBoxFile.Text = openFileDialog1.FileName;
                jsonText = File.ReadAllText(textBoxFile.Text);
                errorProvider.SetError(textBoxFile, string.Empty);
            }
        }

        private void buttonDiscover_Click(object sender, EventArgs e)
        {
            bool valid = true;

            if (textBoxFile.Text != noFile)
            {
                errorProvider.SetError(textBoxFile, string.Empty);
            }
            else if (textBoxURL.Text != noURL)
            {
                ObtainJson();
            }
            else
            {
                errorProvider.SetError(textBoxFile, "A Destination folder must be specified!");
                valid = false;
            }

            if (valid && !string.IsNullOrEmpty(jsonText))
            {
                if (GENERATE)
                {
                    ChangeControlsBeforeDiscovery();
                    backgroundWorker.RunWorkerAsync();
                }
                else
                {
                    MessageBox.Show("Model Discovery is disabled for now. It is being updated for the next version!",
                        "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
        }

        private void ObtainJson()
        {
            if (!string.IsNullOrEmpty(textBoxURL.Text))
            {
                using (HttpClient client = new HttpClient())
                {
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                    HttpResponseMessage response = client.GetAsync(textBoxURL.Text).Result;

                    if (response.IsSuccessStatusCode)
                    {
                        Task<string> result = response.Content.ReadAsStringAsync();
                        jsonText = result.Result;
                    }
                }

                errorProvider.SetError(textBoxURL, string.Empty);
            }
        }

        private void ChangeControlsBeforeDiscovery()
        {
            this.ClientSize = new System.Drawing.Size(399, 155);
            progressBar.Visible = true;
            buttonBrowse.Enabled = false;
            textBoxURL.Enabled = false;
            buttonDiscover.Enabled = false;
            buttonDiscover.Text = "Discovering...";
            buttonDiscover.Size = new System.Drawing.Size(85, 23);
        }

        private void RestoreControlsAfterDiscovery()
        {
            this.ClientSize = new System.Drawing.Size(399, 135);
            progressBar.Visible = false;
            buttonBrowse.Enabled = true;
            textBoxURL.Enabled = true;
            buttonDiscover.Enabled = true;
            buttonDiscover.Text = "Discover!";
            buttonDiscover.Size = new System.Drawing.Size(75, 23);
        }

        private void ExecuteCommand(string command)
        {
            ProcessStartInfo processInfo = new ProcessStartInfo("java.exe", "-jar " + command);
            processInfo.CreateNoWindow = true;
            processInfo.UseShellExecute = false;
            // *** Redirect the output ***
            processInfo.RedirectStandardError = true;
            processInfo.RedirectStandardOutput = true;

            Process process = Process.Start(processInfo);
            process.WaitForExit();

            // *** Read the streams ***
            string output = process.StandardOutput.ReadToEnd();
            string error = process.StandardError.ReadToEnd();

            if (!string.IsNullOrEmpty(output))
            {
                MessageBox.Show("output>>" + output,
                    "",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Information,
                    MessageBoxDefaultButton.Button1);
            }

            if (!string.IsNullOrEmpty(error))
            {
                MessageBox.Show("error>>" + error,
                    "",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Stop,
                    MessageBoxDefaultButton.Button1);
            }
            process.Close();
        }

        #region backgroundWorker Methods

        private void backgroundWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            string exePath = "\"" + Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            string fileName = "result.json";

            using (FileStream fs = File.Create(fileName))
            {
                Byte[] info = new UTF8Encoding(true).GetBytes(jsonText);
                fs.Write(info, 0, info.Length);
            }
            backgroundWorker.ReportProgress(50, new string[] { "JSON read and saved!" });

            ExecuteCommand(exePath + "\\JsonDiscovererCaller.jar\" " + fileName);
            backgroundWorker.ReportProgress(100, new string[] { "Domain Model discovery done!" });
        }

        private void backgroundWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            progressBar.Value = e.ProgressPercentage;
            this.Text = e.ProgressPercentage.ToString() + "%";

            if (e.UserState is string[] && ((string[])e.UserState).Count() > 0)
            {
                this.Text += " - " + ((string[])e.UserState)[0];
            }
        }

        private void backgroundWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            RestoreControlsAfterDiscovery();
        }

        #endregion
    }
}
