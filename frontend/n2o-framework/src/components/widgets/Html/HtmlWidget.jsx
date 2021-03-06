import React from 'react';
import PropTypes from 'prop-types';
import StandardWidget from '../StandardWidget';
import HtmlContainer from './HtmlContainer';
import dependency from '../../../core/dependency';

/**
 * HtmlWidget
 * @reactProps {string} containerId - id конейтенера
 * @reactProps {string} pageId - id страницы
 * @reactProps {boolean} fetchOnInit - фетчить / не фетчить данные при инициализации
 * @reactProps {boolean} url - url для фетчинга
 * @reactProps {string} widgetId - id виджета
 * @reactProps {string} html - html код
 * @reactProps {object} dataProvider
 * @reactProps {object} datasource
 */

function HtmlWidget(props) {
  const {
    fetchOnInit,
    id: widgetId,
    toolbar,
    actions,
    className,
    style,
    pageId,
    datasource,
    ...rest
  } = props;

  return (
    <StandardWidget
      widgetId={widgetId}
      toolbar={toolbar}
      actions={actions}
      className={className}
      fetchOnInit={fetchOnInit}
      style={style}
    >
      <HtmlContainer
        pageId={pageId}
        widgetId={widgetId}
        fetchOnInit={fetchOnInit}
        datasource={datasource}
        {...rest}
      />
    </StandardWidget>
  );
}

HtmlWidget.defaultProps = {
  toolbar: {},
};

HtmlWidget.propTypes = {
  pageId: PropTypes.string,
  fetchOnInit: PropTypes.bool,
  url: PropTypes.bool,
  widgetId: PropTypes.string,
  toolbar: PropTypes.object,
  actions: PropTypes.object,
  html: PropTypes.string,
  dataProvider: PropTypes.object,
};

export default dependency(HtmlWidget);
