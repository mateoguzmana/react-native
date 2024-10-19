/**
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * @format
 * @noflow
 */

'use strict';

const React = require('react');
const {StyleSheet, Text, TouchableHighlight, View} = require('react-native');
const {default: RNTesterText} = require('../../components/RNTesterText');

class XHRExampleHeaders extends React.Component {
  xhr: XMLHttpRequest;
  cancelled: boolean;

  constructor(props) {
    super(props);
    this.cancelled = false;
    this.state = {
      status: '',
      headers: '',
      contentSize: 1,
      downloaded: 0,
    };
  }

  download() {
    this.xhr && this.xhr.abort();

    const xhr = this.xhr || new XMLHttpRequest();
    xhr.onreadystatechange = () => {
      if (xhr.readyState === xhr.DONE) {
        if (this.cancelled) {
          this.cancelled = false;
          return;
        }
        if (xhr.status === 200) {
          this.setState({
            status: 'Download complete!',
            headers: xhr.getAllResponseHeaders(),
          });
        } else if (xhr.status !== 0) {
          this.setState({
            status:
              'Error: Server returned HTTP status of ' +
              xhr.status +
              ' ' +
              xhr.responseText,
          });
        } else {
          this.setState({
            status: 'Error: ' + xhr.responseText,
          });
        }
      }
    };
    xhr.open(
      'GET',
      'https://httpbin.org/response-headers?header1=value&header2=value1&header2=value2',
    );
    xhr.send();
    this.xhr = xhr;

    this.setState({status: 'Downloading...'});
  }

  componentWillUnmount() {
    this.cancelled = true;
    this.xhr && this.xhr.abort();
  }

  render() {
    const button =
      this.state.status === 'Downloading...' ? (
        <View style={styles.wrapper}>
          <View style={styles.button}>
            <Text style={styles.buttonText}>...</Text>
          </View>
        </View>
      ) : (
        <TouchableHighlight
          style={styles.wrapper}
          onPress={this.download.bind(this)}>
          <View style={styles.button}>
            <Text style={styles.buttonText}>Get headers</Text>
          </View>
        </TouchableHighlight>
      );

    return (
      <>
        {button}
        <RNTesterText>{this.state.headers}</RNTesterText>
      </>
    );
  }
}

const styles = StyleSheet.create({
  wrapper: {
    borderRadius: 5,
    marginTop: 5,
  },
  button: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
    backgroundColor: 'blue',
    borderRadius: 4,
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '500',
  },
});

module.exports = XHRExampleHeaders;
