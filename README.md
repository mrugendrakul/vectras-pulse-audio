# Starting module on linux

Use the following code to start module

`pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2
source=alsa_output.pci-0000_00_1b.0.analog-stereo.monitor record=true
port=<your_port> listen=<ip_address>`

In above code change source according to your device