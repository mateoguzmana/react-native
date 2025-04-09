/**
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * @flow
 * @format
 * @oncall react_native
 */

/**
 * The root entry point for type generation (main entry point for the
 * react-native package).
 */
const ENTRY_POINT = 'packages/react-native/index.js.flow';

/**
 * Ignore patterns for source files that should not be considered for
 * translation.
 */
const IGNORE_PATTERNS = [
  '**/__{tests,mocks,fixtures,flowtests}__/**',
  '**/*.{macos,windows}.js',
];

/**
 * The output directory name for generated type definitions in each translated
 * package.
 */
const TYPES_OUTPUT_DIR = 'types_generated';

module.exports = {
  ENTRY_POINT,
  IGNORE_PATTERNS,
  TYPES_OUTPUT_DIR,
};
