import React from 'react';
import { Client } from '@stomp/stompjs';
import JSONPretty from 'react-json-pretty';

const liStyle = {
  textAlign: 'left'
};

class Communication extends React.Component {
  state = {
    serverTime: null,
    greeting: 'Hello World',
    greetings: null,
    transactions: []
  }

  componentDidMount() {
    console.log('Component did mount');
    this.client = new Client();
    this.client.configure({
      brokerURL: 'ws://localhost:8081/stomp',
      onConnect: () => {
        console.log('onConnect');

        this.client.subscribe('/topic/greetings', message => {
            this.setState({greetings: message.body});
        });

        this.client.subscribe('/topic/transactions', message => {
            this.setState({transactions: [...this.state.transactions, message.body]});
        });
      },
      // Helps during debugging, remove in production
      debug: (str) => {
        console.log(new Date(), str);
      }
    });
    this.client.activate();
  }

  clickHandler = () => {
    this.client.publish({destination: '/app/greetings', body: this.state.greeting});
  }

  clickHandlerTest = () => {
     this.setState({
        transactions: [...this.state.transactions, JSON.stringify({ test: "test" })]
     });
  }

  updateGreeting = (evt) => {
      this.setState({greeting: evt.target.value});
  }

  render() {
    return <div>
    <table width="100%">
        <tbody>
            <tr><td valign="top" width="50%">
                <div>
                    Greeting
                </div>
                <div>
                    <input value={this.state.greeting} onChange={this.updateGreeting}/>
                </div>
                <div>
                    <button onClick={this.clickHandler}>Send Greeting</button>
                </div>
                <div>
                    Greetings: {this.state.greetings ? this.state.greetings : ''}
                </div>
            </td>
            <td valign="top">
                <div>
                    <button onClick={this.clickHandlerTest}>Test Transaction Output</button>
                </div>
                <div>
                    Transactions:
                </div>
                <div style={liStyle}>
                    <ul>
                    {this.state.transactions.map((value, index) => {
                        return <li key={index}><JSONPretty id="json-pretty" data={value}></JSONPretty></li>
                    })}
                    </ul>
                </div>
            </td></tr>
        </tbody>
    </table>
</div>;
  }
}

export default Communication;