# Seedbox Client

This little software uses Torrentcloud.eu / Yourseedbox.com as a torrent client and then download all the completed torrents on your machine trough HTTPS.

Meaning that your provider will not see any torrent traffic and will not know the content of your downloads (since we transfer downloaded contents trough https).

Still extremely beta, lacks a couple of important things:

* Gui
* Error handling
* Easy way to run it / compiled version to download

# How to run

Import it into your favorite IDE and run the Main class.

Run it once, it will create a `test.properties` file, put your username, password and download folder in there and then run it again.