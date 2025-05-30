/**
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * @flow strict-local
 * @format
 */

import type {RNTesterModuleExample} from '../../types/RNTesterTypes';

import Playground from './RNTesterPlayground';

export const title = Playground.title;
export const framework = 'React';
export const description = 'Test out new features and ideas.';
export const examples: Array<RNTesterModuleExample> = [Playground];
