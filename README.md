# Environment Set-up
To build the model, I used [Anaconda.](https://www.anaconda.com/products/individual)
The Google Cloud Server should be setup already with Anaconda.
The machine learning code is in a Jupyter notebook.
To get to it, open the Anaconda Navigator located on the desktop.
Once it loads up, you should see a dropdown menu labeled applications on. Select "tensorflow" and once it loads, you can launch Jupyter Notebook.
Open the file called LSTM_Model.ipynb. This notebook is based on another [LSTM model.](https://github.com/dspanah/Human-Activity-Recognition-Keras-Android)
The very last step in the notebook is exporting the model to use in Android.

# Alternative Option
If you can't get the above model working, here is an alternative path:
Ideally, we would like to convert the model to a Tensorflow Lite model, but this isn't supported by the stable version of Tensorflow right now.
Tensorflow does have an experimental API to deploy Tensorflow models Tensorflow Lite. Here are two resources:
[1](https://colab.research.google.com/github/tensorflow/tensorflow/blob/master/tensorflow/lite/experimental/examples/lstm/TensorFlowLite_LSTM_Keras_Tutorial.ipynb) & [2](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite/experimental/examples/lstm/g3doc)


# Database
All of the experimental data is stored in a [Google Sheet.](https://docs.google.com/spreadsheets/d/1yhmhXChoDyuxHTN9C0lE4Yqkj6NtB9YNLL6kHU82SCU/edit#gid=0)
In order for the app to correctly send the data to the database, it has to go through a [Google Script](https://script.google.com/d/1mOljzJ3k6hSVzh4ORDmZln1ZpPkXlqcQytlJpYlyMeKM_-cjPeDxTAyL/edit). The app doesn't directly interact with the Google Sheet.
Everytime you update the script, you need to deploy the new version (Publish -> Deploy Web App). Using Google Sheets, we can easily convert the file to a CSV file
to use in our model.

# Helmet Sensor
Since we don't have the second sensor working, the code for sending it to the script is commented out and missing in some places. The indexes of the commented out code
may need to be modified


