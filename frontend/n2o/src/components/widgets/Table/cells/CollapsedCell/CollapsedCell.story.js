import React from 'react';
import { storiesOf } from '@storybook/react';
import { jsxDecorator } from 'storybook-addon-jsx';
import { number, text, withKnobs } from '@storybook/addon-knobs/react';
import withTests from 'N2oStorybook/withTests';

import Table from '../../Table';
import TextTableHeader from '../../headers/TextTableHeader';
import CollapsedCell from './CollapsedCell';
import CollapsedCellJson from './CollapsedCell.meta.json';
import Factory from '../../../../../core/factory/Factory';

const stories = storiesOf('Ячейки/CollapsedCell', module);

stories.addDecorator(withKnobs);
stories.addDecorator(withTests('CollapsedCell'));
stories.addDecorator(jsxDecorator);
stories.addParameters({
  info: {
    propTables: [CollapsedCell],
    propTablesExclude: [Factory, Table],
  },
});

stories
  .add('Метаданные', () => {
    const props = {
      fieldKey: text('fieldKey', CollapsedCellJson.fieldKey),
      color: text('color', CollapsedCellJson.color),
      amountToGroup: number('amountToGroup', CollapsedCellJson.amountToGroup),
    };

    const tableProps = {
      headers: [
        {
          id: 'id',
          component: TextTableHeader,
          label: 'Ячейка',
        },
      ],
      cells: [
        {
          component: CollapsedCell,
          ...props,
        },
      ],
      datasource: [
        {
          id: 'id',
          name: 'alesha',
          data: ['Казань', 'Москва', 'Токио', 'Берлин', 'Париж', 'Лондон'],
        },
      ],
    };

    return (
      <Table
        headers={tableProps.headers}
        cells={tableProps.cells}
        datasource={tableProps.datasource}
      />
    );
  })

  .add('Компонент', () => {
    const tableProps = {
      headers: [
        {
          id: 'id',
          component: TextTableHeader,
          label: 'Стандартная',
        },
        {
          id: 'id',
          component: TextTableHeader,
          label: 'Цветная',
        },
        {
          id: 'id',
          component: TextTableHeader,
          label: 'Мало элементов',
        },
      ],
      cells: [
        {
          component: CollapsedCell,
          model: {
            data: ['Казань', 'Москва', 'Токио', 'Берлин', 'Париж', 'Лондон'],
          },
          fieldKey: 'data',
        },
        {
          component: CollapsedCell,
          model: {
            data: ['Казань', 'Москва', 'Токио', 'Берлин'],
          },
          fieldKey: 'data',
          color: 'info',
        },
        {
          component: CollapsedCell,
          model: {
            data: ['Казань', 'Москва', 'Токио'],
          },
          fieldKey: 'data',
        },
      ],
      datasource: [
        {
          id: 'id',
          name: 'alesha',
        },
      ],
    };

    return (
      <Table
        headers={tableProps.headers}
        cells={tableProps.cells}
        datasource={tableProps.datasource}
      />
    );
  });
