import React, { useState } from 'react';
import WebSocket from 'ws';
import axios from 'axios';

const ChatApp = () => {
  const [communicationMethod, setCommunicationMethod] = useState('websocket');
  const [message, setMessage] = useState('');

  const handleMethodChange = event => {
    setCommunicationMethod(event.target.value);
  }

  const handleMessageChange = event => {
    setMessage(event.target.value);
  }

  const handleSendMessage = () => {
    if (communicationMethod === 'websocket') {
      const ws = new WebSocket('ws://localhost:3000');
      ws.onopen = () => {
        ws.send(message);
      }
    } else if (communicationMethod === 'polling') {
      setInterval(() => {
        axios.get('http://localhost:3000/message', {
          params: { message }
        });
      }, 1000);
    } else {
      axios.get('http://localhost:3000/message', {
        params: { message }
      })
      .then(response => {
        // handle long polling response
      });
    }
  }

  return (
    <div>
      <div>
        <label>
          <input
            type="radio"
            value="websocket"
            checked={communicationMethod === 'websocket'}
            onChange={handleMethodChange}
          />
          WebSocket
        </label>
        <label>
          <input
            type="radio"
            value="polling"
            checked={communicationMethod === 'polling'}
            onChange={handleMethodChange}
          />
          Polling
        </label>
        <label>
          <input
            type="radio"
            value="long-polling"
            checked={communicationMethod === 'long-polling'}
            onChange={handleMethodChange}
          />
          Long Polling
        </label>
      </div>
      <textarea value={message} onChange={handleMessageChange} />
      <button onClick={handleSendMessage}>Send</button>
    </div>
  );
}

export default ChatApp;