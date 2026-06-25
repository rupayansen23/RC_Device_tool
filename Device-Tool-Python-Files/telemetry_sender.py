import socket
import time

PHONE_IP = "192.168.0.101"
PORT = 6006

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

battery = 100

while True:
    print(f"""---Sending Packets---
        BATTERY:{battery}%,
        RSSI:92%,
        LATENCY:31ms,
        GPS:LOCKED\n""")

    telemetry = [
        f"BATTERY:{battery}%",
        "RSSI:92%",
        "LATENCY:31ms",
        "GPS:LOCKED"
    ]

    for item in telemetry:

        sock.sendto(
            item.encode(),
            (PHONE_IP, PORT)
        )

    battery -= 1

    if battery < 0:
        battery = 100
    
    time.sleep(1)
