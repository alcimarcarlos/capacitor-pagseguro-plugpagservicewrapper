import { WebPlugin } from '@capacitor/core';
import { PlugPagServiceWrapperPlugin } from './definitions';

export class PlugPagServiceWrapperWeb extends WebPlugin implements PlugPagServiceWrapperPlugin {
  constructor() {
    super({
      name: 'PlugPagServiceWrapper',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async getContacts(filter: string): Promise<{ results: any[] }> {
    console.log('Contacts filter', filter);
    return {
      results: []
    };
  }

  async getLibVersion(filter: string): Promise<{ results: any[] }> {
    console.log('LibVersion filter', filter);
    return {
      results: []
    };
  }

  async reprintStablishmentReceipt(filter: string): Promise<{ results: any[] }> {
    console.log('ReprintStablishmentReceipt filter', filter);
    return {
      results: []
    };
  }

  async reprintCustomerReceipt(filter: string): Promise<{ results: any[] }> {
    console.log('ReprintCustomerReceipt filter', filter);
    return {
      results: []
    };
  }

  async getLastApprovedTransaction(filter: string): Promise<{ results: any[] }> {
    console.log('GetLastApprovedTransaction filter', filter);
    return {
      results: []
    };
  }

  async startPayment(filter: string): Promise<{ results: any[] }> {
    console.log('startPayment filter', filter);
    return {
      results: []
    };
  }

}

const PlugPagServiceWrapper = new PlugPagServiceWrapperWeb();

export { PlugPagServiceWrapper };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(PlugPagServiceWrapper);
