import React, { Component } from 'react';
import { pure } from 'recompose';
import PropTypes from 'prop-types';
import AdvancedTableFilterPopup from './AdvancedTableFilterPopup';
import { isEmpty } from 'lodash';
import { Dropdown, DropdownToggle, DropdownMenu, Badge } from 'reactstrap';
import { Button } from 'reactstrap';

/**
 * Компонент заголовок с фильтрацией
 * @param id - id колонки === фильтра
 * @param onFilter - callback на фильтрацию
 * @param children - компонент потомок
 * @param value - предустановленное значение фильтра
 */
class AdvancedTableFilter extends Component {
  constructor(props) {
    super(props);

    this.state = {
      value: props.value || null,
      filterOpen: false,
    };

    this.onChangeFilter = this.onChangeFilter.bind(this);
    this.onResetFilter = this.onResetFilter.bind(this);
    this.onSetFilter = this.onSetFilter.bind(this);
    this.toggleFilter = this.toggleFilter.bind(this);
  }

  toggleFilter() {
    this.setState({ filterOpen: !this.state.filterOpen });
  }

  onChangeFilter(value) {
    this.setState({
      value: value ? value.toString() : '',
    });
  }

  onResetFilter() {
    if (!isEmpty(this.state.value)) {
      const { id, onFilter } = this.props;
      this.setState({ value: '' }, () => onFilter({ id, value: '' }));
    }
  }

  onSetFilter() {
    const { onFilter, id } = this.props;
    onFilter &&
      onFilter({
        id,
        value: this.state.value,
      });
  }

  render() {
    const { children, component } = this.props;
    const { filterOpen, value } = this.state;
    return (
      <React.Fragment>
        {children}
        <Dropdown
          className="n2o-advanced-table-filter-btn"
          isOpen={filterOpen}
          toggle={this.toggleFilter}
        >
          <DropdownToggle tag="div">
            <Button color="link" size="sm">
              <i className="fa fa-filter" />
              {!isEmpty(value) && (
                <Badge
                  className="n2o-advanced-table-filter-badge"
                  color="primary"
                />
              )}
            </Button>
          </DropdownToggle>
          <DropdownMenu
            className="n2o-advanced-table-filter-dropdown"
            tag="div"
            right={true}
          >
            <AdvancedTableFilterPopup
              value={value}
              onChange={this.onChangeFilter}
              onResetFilter={this.onResetFilter}
              onSetFilter={this.onSetFilter}
              component={component}
            />
          </DropdownMenu>
        </Dropdown>
      </React.Fragment>
    );
  }
}

AdvancedTableFilter.propTypes = {
  children: PropTypes.object,
  id: PropTypes.string,
  onFilter: PropTypes.func,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
};

AdvancedTableFilter.defaultProps = {
  onFilter: () => {},
};

export { AdvancedTableFilter };
export default pure(AdvancedTableFilter);