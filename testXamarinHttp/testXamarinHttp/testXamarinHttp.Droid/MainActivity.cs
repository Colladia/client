using System;

using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using System.Threading.Tasks;
using System.Net;
using System.IO;
using System.Json;


namespace testXamarinHttp.Droid
{
	[Activity (Label = "testXamarinHttp.Droid", MainLauncher = true, Icon = "@drawable/icon")]
	public class MainActivity : Activity
	{
        private string server = "10.0.2.2";
        private string jsonToSend =
            "{\"employees\":[" +
                                "{\"firstName\":\"John\", \"lastName\":\"Doe\"}," +
                                "{\"firstName\":\"Anna\", \"lastName\":\"Smith\"}," +
            "]}";

        private const string port = ":8182";

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            // Get the host server ip adress 
            EditText hostIp = FindViewById<EditText>(Resource.Id.hostText);
            hostIp.Text = server;
            Button button = FindViewById<Button>(Resource.Id.sendHttpRequest);

            // When the user clicks the button ...
            button.Click += async (sender, e) => {
                //get the ip server
                server = hostIp.Text;
                //create the url
                string url = "http://" + server + "" + port;

                // Fetch the information asynchronously, 
                JsonValue json = await PostAsync(url, jsonToSend);
                //JsonValue json = await GetAsync(url);

                // parse the results, then update the screen:
                Display(json);
            };
        }













        // Send a http request of POST type, passing the url and the json to send on input       
        private async Task<JsonValue> PostAsync(string url, string jsonInput)
        {
            // Create an HTTP web request using the URL:
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(new Uri(url));
            request.ContentType = "application/json";
            request.Method = "POST";

            //Encode the message to send in UTF8
            byte[] arr = System.Text.Encoding.UTF8.GetBytes(jsonInput.ToString());
            request.ContentLength = arr.Length;

            Stream dataStream = request.GetRequestStream();
            dataStream.Write(arr, 0, arr.Length);
            dataStream.Close();


            // Send the request to the server and wait for the response:
            using (WebResponse response = await request.GetResponseAsync())
            {
                // Get a stream representation of the HTTP web response:
                using (Stream stream = response.GetResponseStream())
                {
                    // Use this stream to build a JSON document object:
                    JsonValue jsonDoc = await Task.Run(() => JsonObject.Load(stream));
                    Console.Out.WriteLine("Response: {0}", response.ToString());

                    // Return the JSON document:
                    return jsonDoc;
                }
            }
        }











        // Send a http request of GET type, passing the url     
        private async Task<JsonValue> GetAsync(string url, string headersInput)
        {
            // Create an HTTP web request using the URL:
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(new Uri(url));
            request.ContentType = "application/json";
            request.Method = "GET";
            //TODO test this way of sending data in get request.Headers["timeStamp"] = headersInput; 

            // Send the request to the server and wait for the response:
            using (WebResponse response = await request.GetResponseAsync())
            {
                // Get a stream representation of the HTTP web response:
                using (Stream stream = response.GetResponseStream())
                {
                    // Use this stream to build a JSON document object:
                    JsonValue jsonDoc = await Task.Run(() => JsonObject.Load(stream));
                    Console.Out.WriteLine("Response: {0}", response.ToString());

                    // Return the JSON document:
                    return jsonDoc;
                }
            }
        }








        // Display
        private void Display(JsonValue json)
        {
        
            TextView response = FindViewById<TextView>(Resource.Id.responseRequestText);
            response.Text = json.ToString();
        }

    }
}


