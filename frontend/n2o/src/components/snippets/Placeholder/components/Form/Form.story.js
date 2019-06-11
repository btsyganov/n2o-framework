import React from 'react';
import { storiesOf } from '@storybook/react';
import { withKnobs, select, boolean, text } from '@storybook/addon-knobs/react';
import Placeholder from '../../Placeholder';
import meta from './Form.meta';

const stories = storiesOf('UI Компоненты/Placeholder/type=form', module);

stories.addDecorator(withKnobs);

stories.add('Компонент', () => {
  const props = {
    loading: boolean('loading', true),
    type: text('type', meta.type),
    rows: select('rows', [1, 2, 3, 4, 5], meta.rows),
    cols: select('cols', [1, 2, 3, 4, 5], meta.cols),
  };

  return <Placeholder {...props} />;
});