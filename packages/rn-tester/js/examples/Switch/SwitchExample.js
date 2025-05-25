/**
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * @flow
 * @format
 */

'use strict';

import RNTesterText from '../../components/RNTesterText';
import React from 'react';
import {useState} from 'react';
import {Alert, Platform, Switch, View} from 'react-native';

type OnOffIndicatorProps = $ReadOnly<{on: boolean, testID: string}>;
function OnOffIndicator({on, testID}: OnOffIndicatorProps) {
  return <RNTesterText testID={testID}>{on ? 'On' : 'Off'}</RNTesterText>;
}

type ExampleRowProps = $ReadOnly<{children: React.Node}>;
function ExampleRow({children}: ExampleRowProps) {
  return (
    <View
      style={{
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 10,
      }}>
      {children}
    </View>
  );
}

function BasicSwitchExample(): React.Node {
  const [trueSwitchIsOn, setTrueSwitchIsOn] = useState<boolean>(true);
  const [falseSwitchIsOn, setFalseSwitchIsOn] = useState<boolean>(false);

  return (
    <View>
      <ExampleRow>
        <Switch
          testID="on-off-initial-off"
          onValueChange={value => setFalseSwitchIsOn(value)}
          trackColor={{
            true: 'yellow',
            false: 'purple',
          }}
          value={falseSwitchIsOn}
        />
        <OnOffIndicator
          on={falseSwitchIsOn}
          testID="on-off-initial-off-indicator"
        />
      </ExampleRow>
      <ExampleRow>
        <Switch
          testID="on-off-initial-on"
          onValueChange={value => setTrueSwitchIsOn(value)}
          value={trueSwitchIsOn}
        />
        <OnOffIndicator
          on={trueSwitchIsOn}
          testID="on-off-initial-on-indicator"
        />
      </ExampleRow>
    </View>
  );
}

function DisabledSwitchExample(): React.Node {
  const [trueSwitchIsOn, setTrueSwitchIsOn] = useState<boolean>(true);
  const [falseSwitchIsOn, setFalseSwitchIsOn] = useState<boolean>(false);

  return (
    <View>
      <ExampleRow>
        <Switch
          testID="disabled-initial-off"
          disabled={true}
          onValueChange={value => setFalseSwitchIsOn(value)}
          value={falseSwitchIsOn}
        />

        <OnOffIndicator
          on={falseSwitchIsOn}
          testID="disabled-initial-off-indicator"
        />
      </ExampleRow>

      <ExampleRow>
        <Switch
          testID="disabled-initial-on"
          disabled={true}
          onValueChange={value => setTrueSwitchIsOn(value)}
          value={trueSwitchIsOn}
        />

        <OnOffIndicator
          on={trueSwitchIsOn}
          testID="disabled-initial-on-indicator"
        />
      </ExampleRow>
    </View>
  );
}

function ColorSwitchExample(): React.Node {
  const [colorFalseSwitchIsOn, setColorFalseSwitchIsOn] =
    useState<boolean>(false);
  const [colorTrueSwitchIsOn, setColorTrueSwitchIsOn] = useState<boolean>(true);

  return (
    <View>
      <Switch
        testID="initial-false-switch"
        onValueChange={value => setColorFalseSwitchIsOn(value)}
        style={{marginBottom: 10}}
        thumbColor="#0000ff"
        trackColor={{
          false: '#ff0000',
          true: '#00ff00',
        }}
        value={colorFalseSwitchIsOn}
      />
      <Switch
        testID="initial-true-switch"
        onValueChange={value => setColorTrueSwitchIsOn(value)}
        thumbColor="#0000ff"
        trackColor={{
          false: '#ff0000',
          true: '#00ff00',
        }}
        value={colorTrueSwitchIsOn}
      />
    </View>
  );
}

function EventSwitchExample(): React.Node {
  const [eventSwitchIsOn, setEventSwitchIsOn] = useState<boolean>(false);
  const [eventSwitchRegressionIsOn, setEventSwitchRegressionIsOn] =
    useState<boolean>(true);

  return (
    <View style={{flexDirection: 'row', justifyContent: 'space-around'}}>
      <View>
        <Switch
          testID="event-switch-top"
          onValueChange={value => setEventSwitchIsOn(value)}
          style={{marginBottom: 10}}
          value={eventSwitchIsOn}
        />
        <Switch
          testID="event-switch-bottom"
          onValueChange={value => setEventSwitchIsOn(value)}
          style={{marginBottom: 10}}
          value={eventSwitchIsOn}
        />
        <RNTesterText testID="event-switch-indicator">
          {eventSwitchIsOn ? 'On' : 'Off'}
        </RNTesterText>
      </View>
      <View>
        <Switch
          testID="event-switch-regression-top"
          onValueChange={value => setEventSwitchRegressionIsOn(value)}
          style={{marginBottom: 10}}
          value={eventSwitchRegressionIsOn}
        />
        <Switch
          testID="event-switch-regression-bottom"
          onValueChange={value => setEventSwitchRegressionIsOn(value)}
          style={{marginBottom: 10}}
          value={eventSwitchRegressionIsOn}
        />
        <RNTesterText testID="event-switch-regression-indicator">
          {eventSwitchRegressionIsOn ? 'On' : 'Off'}
        </RNTesterText>
      </View>
    </View>
  );
}

function IOSBackgroundColEx(): React.Node {
  return (
    <View>
      <Switch
        disabled
        ios_backgroundColor="#ffa500"
        style={{marginBottom: 20}}
      />
      <RNTesterText>
        The background color can be seen either when the switch value is false
        or when the switch is disabled (and the switch is translucent).{' '}
      </RNTesterText>
    </View>
  );
}

function OnChangeExample(): React.Node {
  return (
    <View>
      <Switch
        onChange={() => {
          Alert.alert('OnChange Called');
        }}
      />
    </View>
  );
}

class ContainerBackgroundColorStyleExample extends React.Component<
  {...},
  $FlowFixMeState,
> {
  render(): React.Node {
    return (
      <View>
        <Switch
          style={{backgroundColor: 'blue'}}
          thumbColor="white"
          value={true}
        />
      </View>
    );
  }
}

exports.title = 'Switch';
exports.documentationURL = 'https://reactnative.dev/docs/switch';
exports.category = 'UI';
exports.displayName = 'SwitchExample';
exports.description = 'Native boolean input';
exports.examples = [
  {
    title: 'Switches can be set to true or false',
    name: 'basic',
    render(): React.MixedElement {
      return <BasicSwitchExample />;
    },
  },
  {
    title: 'Switches can be disabled',
    name: 'disabled',
    render(): React.MixedElement {
      return <DisabledSwitchExample />;
    },
  },
  {
    title: 'Change events can be detected',
    name: 'events',
    render(): React.MixedElement {
      return <EventSwitchExample />;
    },
  },
  {
    title: 'Switches are controlled components',
    name: 'controlled',
    render(): React.MixedElement {
      return <Switch testID="controlled-switch" />;
    },
  },
  {
    title: 'Custom colors can be provided',
    name: 'custom-colors',
    render(): React.MixedElement {
      return <ColorSwitchExample />;
    },
  },
  {
    title: 'OnChange receives the change event as an argument',
    render(): React.MixedElement {
      return <OnChangeExample />;
    },
  },
  {
    title: "The container's background color can be set",
    render(): React.MixedElement {
      return <ContainerBackgroundColorStyleExample />;
    },
  },
];

if (Platform.OS === 'ios') {
  /* $FlowFixMe[incompatible-call] error found during natural inference roll-
   * out. See https://fburl.com/workplace/tc9m3tcf */
  exports.examples.push({
    title: '[iOS Only] Custom background colors can be set',
    render(): React.MixedElement {
      return <IOSBackgroundColEx />;
    },
  });
}
