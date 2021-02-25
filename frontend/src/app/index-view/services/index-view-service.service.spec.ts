import { TestBed } from '@angular/core/testing';

import { IndexViewServiceService } from './index-view-service.service';

describe('IndexViewServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: IndexViewServiceService = TestBed.get(IndexViewServiceService);
    expect(service).toBeTruthy();
  });
});
