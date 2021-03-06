import isObject from 'lodash/isObject';
import isArray from 'lodash/isArray';
import values from 'lodash/values';
import isString from 'lodash/isString';
import merge from 'deepmerge';

import headers from '../../components/widgets/Table/headers';
import cells from '../../components/widgets/Table/cells';
import fieldsets from '../../components/widgets/Form/fields';
import fields from '../../components/widgets/Form/fieldsets';
import actions from '../../impl/actions';
import exportModal from '../../components/widgets/Table/ExportModal';
import storyModal from '../../components/widgets/Table/StoryModal';
import ToggleColumn from '../../components/buttons/ToggleColumn/ToggleColumn';
import ChangeSize from '../../components/buttons/ChangeSize/ChangeSize';
import controls from '../../components/controls';
import NotFoundFactory from '../../core/factory/NotFoundFactory';

const index = {
  ...headers,
  ...cells,
  ...fieldsets,
  ...fields,
  ...actions,
  ...controls,
  exportModal,
  storyModal,
  ToggleColumn,
  ChangeSize,
  NotFoundFactory,
};

const functionType = 'function';
const componentType = 'component';

/**
 * Функция преобразует ссылку в метаданных на компонент, на React компонент.
 * Производит поиск свойства src и заменяет его на свойство component, где значение компонент из компонентных карт.
 * @param {Object} props - объект свойств которые требуется преобразовать
 * @param {String} defaultComponent - src-string по-умолчанию
 * @param {String} type - тип преобразуемого объекта
 * @param {object} customConfig - кастомные компоненты
 * @return {Object}
 * @example
 * const props = {
 *  widgetId: "Test.test",
 *  src: "TableWidget"
 * }
 *
 * console.log(factoryResolver(props, 'widgets'))
 *
 * //- {widgetId: "Test.test", component: TableWidget}
 */
export default function factoryResolver(
  props,
  defaultComponent = 'NotFoundFactory',
  type = componentType,
  customConfig = {}
) {
  const config = merge(index, customConfig);
  let obj = {};
  if (isObject(props)) {
    Object.keys(props).map(key => {
      if (isObject(props[key])) {
        obj[key] = factoryResolver(props[key]);
      } else if (key === 'src') {
        obj[type] = config[props[key]] || config[defaultComponent];
      } else {
        obj[key] = props[key];
      }
    });
    return isArray(props) ? values(obj) : obj;
  } else if (isString(props)) {
    return config[props] || config[defaultComponent];
  }
  return props;
}
