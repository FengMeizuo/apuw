const express = require('express');
const WebSocket = require('ws');

const app = express();
const wss = new WebSocket.Server({ port: 3000 });

wss.on('connection', (ws) => {
  ws.on('message', (message) => {
    console.log(`Received message: ${message}`);
    ws.send(`Echo: ${message}`);
  });
});

app.get('/message', (req, res) => {
  const { message } = req.query;
  console.log(`Received message: ${message}`);
  setTimeout(() => {
    res.send(`Echo: ${message}`);
  }, 5000);
});

app.listen(3000, () => {
  console.log('Server started on port 3000');
});