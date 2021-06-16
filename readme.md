## Introduction
A simple example to simulate pop3 and smtp command modes.
Reference [RFC1939](https://www.rfc-editor.org/rfc/rfc1939) and [RFC821](https://www.rfc-editor.org/rfc/rfc821).

## Usage
- `mvn package`
- start server :`java -jar .\target\EmailServer-jar-with-dependencies.jar`
- Open a new terminal and `telnet localhost 25` to connect to the SMTP server.
- Open a new terminal and `telnet localhost 110` to connect to the POP3 server.


## More info

I hardcoded the SMPT server address to **smtp.fyc.com** and the POP3 address to **pop.fyc.com**. This means that emails such as xxx@fyc.com are stored on this fake server and subsequent POP3s are pulled based on this email suffix as well.
Again, for convenience, I hard-coded the accounts, which I know isn't good.
- username ： sloth@fyc.com
- password  :    123